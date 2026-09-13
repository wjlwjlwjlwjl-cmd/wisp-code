package com.nexus.nexusportalservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nexus.nexuscommoncore.domain.dto.BasePageDTO;
import com.nexus.nexuscommoncore.utils.BeanCopyUtil;
import com.nexus.nexuscommondomain.domain.dto.LoginUserDTO;
import com.nexus.nexuscommonsecurity.service.TokenService;
import com.nexus.nexusportalservice.domain.dto.DeployAppDTO;
import com.nexus.nexusportalservice.domain.entity.App;
import com.nexus.nexusportalservice.domain.vo.AppVO;
import com.nexus.nexusportalservice.mapper.AppMapper;
import com.nexus.nexusportalservice.service.IAppBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AppBaseServiceImpl implements IAppBaseService {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private AppMapper appMapper;

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

        LoginUserDTO loginUserDTO = tokenService.getLoginUser(token);
        String userId = loginUserDTO.getUserId();

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
        basePageDTO.setList(appVOS);
        basePageDTO.setCurrent((int) rets.getCurrent());
        basePageDTO.setPageSize((int) rets.getSize());
        basePageDTO.setTotals(appMapper.getMyAppNum(userId));

        return basePageDTO;
    }

    @Override
    public DeployAppDTO appDeploy(String token, String appId, Boolean deploy) {
        DeployAppDTO deployAppDTO = new DeployAppDTO();
        String userId = tokenService.getLoginUser(token).getUserId();
        LambdaUpdateWrapper<App> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper
                .eq(App::getId, appId)
                .eq(App::getUserId, userId)
                .set(App::getDeploy, deploy);
        int cnt = appMapper.update(lambdaUpdateWrapper);
        if(cnt == 0){
            deployAppDTO.setSuccess(false);
            deployAppDTO.setErrMsg("部署失败，检查 userId 及 appId");
            return deployAppDTO;
        }
        deployAppDTO.setSuccess(true);
        return deployAppDTO;
    }
}
