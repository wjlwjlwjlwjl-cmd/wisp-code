package com.nexus.nexusadminservice.user.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.nexus.nexusadminapi.appuser.domain.dto.AppUserDTO;
import com.nexus.nexusadminapi.appuser.domain.dto.AppUserListReqDTO;
import com.nexus.nexusadminapi.appuser.domain.dto.UserEditReqDTO;
import com.nexus.nexusadminservice.user.config.RabbitConfig;
import com.nexus.nexusadminservice.user.dao.AppUserDao;
import com.nexus.nexusadminservice.user.domain.entity.AppUser;
import com.nexus.nexusadminservice.user.service.IAppUserService;
import com.nexus.nexuscommoncore.domain.dto.BasePageDTO;
import com.nexus.nexuscommoncore.utils.AESUtil;
import com.nexus.nexuscommoncore.utils.BeanCopyUtil;
import com.nexus.nexuscommondomain.domain.ResultCode;
import com.nexus.nexuscommondomain.exception.ServiceException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RefreshScope
public class AppUserServiceImpl implements IAppUserService {
    @Autowired
    private AppUserDao appUserDao;

    @Value("${appuser.info.defaultAvatar}")
    private String defaultAvatar;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public AppUserDTO registerByEmail(String email) throws ServiceException {
        if (StringUtils.isEmpty(email)) {
            throw new ServiceException("邮箱不能为空", ResultCode.INVALID_PARA.getCode());
        }
        AppUser appUser = new AppUser();
        appUser.setEmail(email);
        appUser.setNickName("Nexus用户"+ (int) (Math.random() * 9000) + 1000);
        appUser.setAvatar(defaultAvatar);
        appUserDao.insert(appUser);
        AppUserDTO appUserDTO = new AppUserDTO();
        BeanCopyUtil.copyProperties(appUser, appUserDTO);
        appUserDTO.setUserId(appUser.getId());
        return appUserDTO;
    }

    @Override
    public AppUserDTO findByEmail(String email) {
        if (StringUtils.isEmpty(email)) {
            return null;
        }

        // 1 查询appUser实体类
        AppUser appUser = appUserDao.selectByEmail(email);

        // 2 对查出来的结果进行判断
        if (appUser == null) {
            return null;
        }
        AppUserDTO appUserDTO = new AppUserDTO();
        BeanCopyUtil.copyProperties(appUser, appUserDTO);
        // 3 处理手机号
        appUserDTO.setPhoneNumber(AESUtil.decryptHex(appUser.getPhoneNumber()));
        return appUserDTO;
    }

    @Override
    public Void edit(UserEditReqDTO userEditReqDTO) throws ServiceException{
        // 1 根据ID查询要编辑的用户
        AppUser appUser = appUserDao.selectById(userEditReqDTO.getUserId());
        if (appUser == null) {
            throw new ServiceException("用户不存在", ResultCode.INVALID_PARA.getCode());
        }
        // 2 查到用户，进行编辑操作
        appUser.setNickName(userEditReqDTO.getNickName());
        appUser.setAvatar(userEditReqDTO.getAvtar());
        appUserDao.updateById(appUser);
        // 3 发送广播消息
        AppUserDTO appUserDTO = new AppUserDTO();
        BeanCopyUtil.copyProperties(appUser, appUserDTO);
        appUserDTO.setUserId(appUser.getId());
        try {
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, "", appUserDTO);
        } catch (Exception exception) {
            log.error("编辑用户发送消息失败", exception);
        }
        return null;
    }

    @Override
    public BasePageDTO<AppUserDTO> getUserList(AppUserListReqDTO appUserListReqDTO) {
        // 1 先把手机号转变过来
        appUserListReqDTO.setPhoneNumber(AESUtil.encryptHex(appUserListReqDTO.getPhoneNumber()));
        BasePageDTO<AppUserDTO> result = new BasePageDTO<AppUserDTO>();

        // 2 查询总数
        Long totals = appUserDao.selectCount(appUserListReqDTO);
        if (totals == 0) {
            result.setTotals(0);
            result.setTotalPages(0);
            result.setList(new ArrayList<>());
            return result;
        }

        // 3 分页查询
        List<AppUser> appUserList = appUserDao.selectPage(appUserListReqDTO);
        result.setTotals(totals.intValue());
        result.setTotalPages(
                BasePageDTO.calculateTotalPages(totals, appUserListReqDTO.getPageSize())
        );
        // 4 超页
        if (CollectionUtils.isEmpty(appUserList)) {
            result.setList(new ArrayList<>());
            return result;
        }
        // 5 对象列表结果转换
        result.setList(
                appUserList.stream()
                        .map(appUser -> {
                            AppUserDTO appUserDTO = new AppUserDTO();
                            BeanCopyUtil.copyProperties(appUser, appUserDTO);
                            appUserDTO.setUserId(appUser.getId());
                            appUserDTO.setPhoneNumber(AESUtil.decryptHex(appUser.getPhoneNumber()));
                            return appUserDTO;
                        }).collect(Collectors.toList())
        );
        return result;
    }

    @Override
    public AppUserDTO findById(Long userId) {
        // 1 对userId进行判空操作
        if (userId == null) {
            return null;
        }
        // 2 查询appUser对象
        AppUser appUser = appUserDao.selectById(userId);
        if (appUser == null) {
            return null;
        }
        // 3 对象转换
        AppUserDTO appUserDTO = new AppUserDTO();
        BeanCopyUtil.copyProperties(appUser, appUserDTO);
        appUserDTO.setPhoneNumber(AESUtil.decryptHex(appUser.getPhoneNumber()));
        appUserDTO.setUserId(appUser.getId());
        return appUserDTO;
    }

    @Override
    public List<AppUserDTO> getUserList(List<Long> userIds) {
        // 1 对入参进行判空
        if (CollectionUtils.isEmpty(userIds)) {
            return Arrays.asList();
        }
        // 2 查询appUser列表
        List<AppUser> appUserList = appUserDao.selectBatchIds(userIds);

        // 3 对象转换
        return appUserList.stream()
                .map(appUser -> {
                    AppUserDTO appUserDTO = new AppUserDTO();
                    BeanCopyUtil.copyProperties(appUser, appUserDTO);
                    appUserDTO.setPhoneNumber(AESUtil.decryptHex(appUser.getPhoneNumber()));
                    appUserDTO.setUserId(appUser.getId());
                    return appUserDTO;
                }).collect(Collectors.toList());
    }

}
