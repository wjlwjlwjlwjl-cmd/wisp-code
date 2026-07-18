package com.nexus.nexusadminservice.user.service;

import java.util.List;

import com.nexus.nexusadminapi.appuser.domain.dto.AppUserDTO;
import com.nexus.nexusadminapi.appuser.domain.dto.AppUserListReqDTO;
import com.nexus.nexusadminapi.appuser.domain.dto.UserEditReqDTO;
import com.nexus.nexuscommoncore.domain.dto.BasePageDTO;
import com.nexus.nexuscommondomain.exception.ServiceException;

public interface IAppUserService {
    AppUserDTO registerByEmail (String email) throws ServiceException;

    AppUserDTO findByEmail(String email);

    Void edit(UserEditReqDTO userEditReqDTO) throws ServiceException;

    BasePageDTO<AppUserDTO> getUserList(AppUserListReqDTO appUserListReqDTO);

    AppUserDTO findById(Long userId);

    List<AppUserDTO> getUserList(List<Long> userIds);
}