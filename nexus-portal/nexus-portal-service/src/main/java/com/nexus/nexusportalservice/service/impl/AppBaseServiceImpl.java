package com.nexus.nexusportalservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nexus.nexuscommoncore.domain.dto.BasePageDTO;
import com.nexus.nexuscommoncore.utils.BeanCopyUtil;
import com.nexus.nexuscommondomain.constants.TokenConstants;
import com.nexus.nexuscommondomain.domain.dto.LoginUserDTO;
import com.nexus.nexuscommonredis.service.RedisService;
import com.nexus.nexuscommonsecurity.service.TokenService;
import com.nexus.nexusportalservice.domain.dto.AppDTO;
import com.nexus.nexusportalservice.domain.dto.ChatHistoryDTO;
import com.nexus.nexusportalservice.domain.dto.DeployAppDTO;
import com.nexus.nexusportalservice.domain.entity.App;
import com.nexus.nexusportalservice.domain.entity.EmailUser;
import com.nexus.nexusportalservice.domain.entity.Memory;
import com.nexus.nexusportalservice.domain.vo.AppVO;
import com.nexus.nexusportalservice.mapper.AppMapper;
import com.nexus.nexusportalservice.mapper.EmailUserMapper;
import com.nexus.nexusportalservice.mapper.MemoryMapper;
import com.nexus.nexusportalservice.service.IAppBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AppBaseServiceImpl implements IAppBaseService {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private AppMapper appMapper;
    @Autowired
    private MemoryMapper memoryMapper;
    @Autowired
    private EmailUserMapper emailUserMapper;
    @Autowired
    private RedisService redisService;

    /**
     * 根据 AppVO 列表中的 userId 批量回填用户名
     */
    private void fillUserNames(List<AppVO> appVOS) {
        if (appVOS == null || appVOS.isEmpty()) {
            return;
        }
        List<String> userIds = appVOS.stream()
                .map(AppVO::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (userIds.isEmpty()) {
            return;
        }
        List<EmailUser> users = emailUserMapper.selectList(
                new LambdaQueryWrapper<EmailUser>().in(EmailUser::getUserId, userIds)
        );
        Map<String, String> id2name = new HashMap<>();
        for (EmailUser user : users) {
            id2name.put(user.getUserId(), user.getUsername());
        }
        for (AppVO appVO : appVOS) {
            appVO.setUsername(id2name.get(appVO.getUserId()));
        }
    }

    @Override
    //获取 user 的所有应用
    public BasePageDTO<AppVO> listMyApp(String token, Integer current, Integer pageSize) {
        BasePageDTO<AppVO> basePageDTO = new BasePageDTO<>();
        List<AppVO> appVOS = new ArrayList<>();

        LoginUserDTO loginUserDTO = tokenService.getLoginUser(token);
        String userId = loginUserDTO.getUserId();

        Page<App> page = new Page<>(current, pageSize);
        Page<App> rets = appMapper.selectPage(page, new LambdaQueryWrapper<App>()
                .eq(App::getUserId, userId)
        );
        List<App> apps = rets.getRecords();

        for(App app: apps){
            System.out.println(app.getId());
            AppVO appVO = new AppVO();
            BeanCopyUtil.copyProperties(app, appVO);
            appVOS.add(appVO);
        }
        fillUserNames(appVOS);
        basePageDTO.setList(appVOS);
        basePageDTO.setCurrent((int) rets.getCurrent());
        basePageDTO.setPageSize((int) rets.getSize());
        basePageDTO.setTotals(appMapper.getMyAppNum(userId));

        return basePageDTO;
    }

    @Override
    public BasePageDTO<AppVO> listDeployApp(String token, Integer current, Integer pageSize) {
        BasePageDTO<AppVO> basePageDTO = new BasePageDTO<>();
        List<AppVO> appVOS = new ArrayList<>();

        Page<App> page = new Page<>(current, pageSize);
        Page<App> rets = appMapper.selectPage(page, new LambdaQueryWrapper<App>()
                .eq(App::getDeploy, true)
        );
        List<App> apps = rets.getRecords();

        for(App app: apps){
            AppVO appVO = new AppVO();
            BeanCopyUtil.copyProperties(app, appVO);
            appVOS.add(appVO);
        }
        fillUserNames(appVOS);
        basePageDTO.setList(appVOS);
        basePageDTO.setCurrent((int) rets.getCurrent());
        basePageDTO.setPageSize((int) rets.getSize());
        basePageDTO.setTotals((int) rets.getTotal());

        return basePageDTO;
    }

    @Override
    public DeployAppDTO appDeploy(String token, String appId, Boolean deploy) {
        DeployAppDTO deployAppDTO = new DeployAppDTO();
        String userId = tokenService.getLoginUser(token).getUserId();

        if(!redisService.hasKey(TokenConstants.LOGIN_TOKEN_KEY + userId)){
            deployAppDTO.setSuccess(false);
            deployAppDTO.setErrMsg("部署失败，检查 userId 及 appId");
            return deployAppDTO;
        }
        appMapper.update(new LambdaUpdateWrapper<App>()
                .eq(App::getId, appId)
                .set(App::getDeploy, deploy)
        );
        deployAppDTO.setSuccess(true);
        return deployAppDTO;
    }

    @Override
    public AppDTO appDetail(String appId) {
        AppDTO appDTO = new AppDTO();

        App app = appMapper.selectOne(new LambdaQueryWrapper<App>()
                .eq(App::getId, appId)
        );

        if(app == null){
            return appDTO;
        }
        BeanCopyUtil.copyProperties(app, appDTO);
        // 回填 owner 用户名（前端用用户名表示身份，不展示 userId）
        EmailUser owner = emailUserMapper.selectOne(
                new LambdaQueryWrapper<EmailUser>().eq(EmailUser::getUserId, app.getUserId())
        );
        if (owner != null) {
            appDTO.setUsername(owner.getUsername());
        }
        return appDTO;
    }

    @Override
    public List<ChatHistoryDTO> appHistory(String appId) {
        Page<Memory> page = new Page<>(1, Long.MAX_VALUE);

        Page<Memory> rets = memoryMapper.selectPage(page, new LambdaQueryWrapper<Memory>()
                .eq(Memory::getAppId, appId)
        );
        List<Memory> apps = rets.getRecords();
        return BeanCopyUtil.copyListProperties(apps, ChatHistoryDTO::new);
    }
}
