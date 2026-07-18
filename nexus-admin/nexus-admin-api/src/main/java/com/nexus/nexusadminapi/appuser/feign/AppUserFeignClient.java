package com.nexus.nexusadminapi.appuser.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.nexus.nexusadminapi.appuser.domain.dto.UserEditReqDTO;
import com.nexus.nexusadminapi.appuser.domain.vo.AppUserVo;
import com.nexus.nexuscommondomain.domain.R;
import com.nexus.nexuscommondomain.exception.ServiceException;

import java.util.List;

/**
 * C端用户数据操作远程调用
 */
@FeignClient(contextId = "appUserFeignClient", value = "nexus-admin", path = "/app_user")
public interface AppUserFeignClient {

    /**
     * 根据邮箱注册用户
     * @param email 用户邮箱
     * @return C端用户VO
     * @throws ServiceException 
     */
    @GetMapping("/register/email")
    R<AppUserVo> registerByEmail(@RequestParam String email) throws ServiceException;

    /**
     * 根据email查询用户信息
     * @param email 用户邮箱
     * @return C端用户VO
     */
    @GetMapping("/email_find")
    R<AppUserVo> findByEmail(@RequestParam String email);

    /**
     * 编辑C端用户
     * @param userEditReqDTO C端用户DTO
     * @return void
     * @throws ServiceException 
     */
    @PostMapping("/edit")
    R<Void> edit(@RequestBody @Validated UserEditReqDTO userEditReqDTO) throws ServiceException;

    /**
     * 根据用户ID获取用户信息
     * @param userId 用户ID
     * @return C端用户VO
     */
    @GetMapping("/id_find")
    R<AppUserVo> findById(@RequestParam Long userId);

    /**
     * 根据用户ID列表获取用户列表信息
     * @param userIds 用户ID列表
     * @return C端用户VO列表
     */
    @PostMapping("/list")
    R<List<AppUserVo>> list(@RequestBody List<Long> userIds);
}
