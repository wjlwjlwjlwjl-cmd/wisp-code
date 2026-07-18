package com.nexus.nexusadminservice.user.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nexus.nexusadminapi.appuser.domain.dto.AppUserDTO;
import com.nexus.nexusadminapi.appuser.domain.dto.AppUserListReqDTO;
import com.nexus.nexusadminapi.appuser.domain.dto.UserEditReqDTO;
import com.nexus.nexusadminapi.appuser.domain.vo.AppUserVo;
import com.nexus.nexusadminapi.appuser.feign.AppUserFeignClient;
import com.nexus.nexusadminservice.user.service.impl.AppUserServiceImpl;
import com.nexus.nexuscommoncore.domain.dto.BasePageDTO;
import com.nexus.nexuscommoncore.utils.BeanCopyUtil;
import com.nexus.nexuscommondomain.domain.R;
import com.nexus.nexuscommondomain.domain.vo.BasePageVO;
import com.nexus.nexuscommondomain.exception.ServiceException;

@RestController
public class AppUserController implements AppUserFeignClient{
    @Autowired
    AppUserServiceImpl appUserServiceImpl;

    @Override
    public R<AppUserVo> registerByEmail(String email) throws ServiceException{
        AppUserVo appUserVo = new AppUserVo();
        AppUserDTO appUserDTO = appUserServiceImpl.registerByEmail(email);
        BeanCopyUtil.copyProperties(appUserDTO, appUserVo);
        return R.ok(appUserVo);
    }

    @Override
    public R<AppUserVo> findByEmail(String email) {
        AppUserVo appUserVo = new AppUserVo();
        BeanCopyUtil.copyProperties(appUserServiceImpl.findByEmail(email), appUserVo);
        return R.ok(appUserVo);
    }

    @Override
    public R<Void> edit(UserEditReqDTO userEditReqDTO) throws ServiceException{
        appUserServiceImpl.edit(userEditReqDTO);
        return R.ok(null);
    }

    @Override
    public R<AppUserVo> findById(Long userId) {
        AppUserVo appUserVo = new AppUserVo();
        BeanCopyUtil.copyProperties(appUserServiceImpl.findById(userId), appUserVo);
        return R.ok(appUserVo);
    }

    @Override
    public R<List<AppUserVo>> list(List<Long> userIds) {
        List<AppUserVo> appUserVos = new ArrayList<>();
        BeanCopyUtil.copyProperties(appUserServiceImpl.getUserList(userIds), appUserVos);
        return R.ok(appUserVos);
    }
    
    /**
     * 查询C端用户
     * @param appUserListReqDTO 查询C端用户DTO
     * @return C端用户分页结果
     */
    @PostMapping("/list/search")
    public R<BasePageVO<AppUserVo>> list(@RequestBody AppUserListReqDTO appUserListReqDTO) {
        BasePageDTO<AppUserDTO> appUserDTOList = appUserServiceImpl.getUserList(appUserListReqDTO);
        BasePageVO<AppUserVo> result = new BasePageVO<>();
        BeanCopyUtil.copyProperties(appUserDTOList, result);
        return R.ok(result);
    }
}
