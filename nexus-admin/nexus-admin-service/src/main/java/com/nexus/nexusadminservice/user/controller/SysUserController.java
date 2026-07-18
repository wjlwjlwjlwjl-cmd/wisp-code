package com.nexus.nexusadminservice.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.nexus.nexusadminservice.user.domain.dto.PasswordLoginDTO;
import com.nexus.nexusadminservice.user.domain.dto.SysUserDTO;
import com.nexus.nexusadminservice.user.domain.dto.SysUserListReqDTO;
import com.nexus.nexusadminservice.user.domain.vo.SysUserLoginVO;
import com.nexus.nexusadminservice.user.domain.vo.SysUserVo;
import com.nexus.nexusadminservice.user.service.ISysUserService;
import com.nexus.nexuscommondomain.domain.R;
import com.nexus.nexuscommondomain.domain.vo.TokenVO;
import com.nexus.nexuscommondomain.exception.ServiceException;
import com.nexus.nexuscommonsecurity.domain.dto.TokenDTO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * B端用户服务控制器类
 */
@RestController
@RequestMapping("/sys_user")
public class SysUserController {

    @Autowired
    private ISysUserService sysUserService;

    /**
     * B端用户登录（手机号+密码）
     *
     * @param passwordLoginDTO B端用户登录DTO
     * @return token信息（访问令牌与过期时间）
     * @throws ServiceException 
     */
    @PostMapping("/login/password")
    public R<TokenVO> login(@Validated @RequestBody PasswordLoginDTO passwordLoginDTO) throws ServiceException {
        TokenDTO tokenDTO = sysUserService.login(passwordLoginDTO);
        return R.ok(tokenDTO.convertToVo());
    }

    /**
     * 新增或编辑用户
     * @param sysUserDTO B端用户信息
     * @return  用户ID
     * @throws ServiceException 
     */
    @PostMapping("/add_edit")
    public R<Long> addOrEditUser(@RequestBody SysUserDTO sysUserDTO) throws ServiceException {
        return R.ok(sysUserService.addOrEdit(sysUserDTO));
    }

    /**
     * 查询B端用户
     * @param sysUserListReqDTO 用户查询DTO
     * @return B用户列表
     */
    @PostMapping("/list")
    public R<List<SysUserVo>> getUserList(@RequestBody SysUserListReqDTO sysUserListReqDTO) {
        List<SysUserDTO> sysUserDTOS = sysUserService.getUserList(sysUserListReqDTO);
        return R.ok(sysUserDTOS.stream()
                .map(SysUserDTO::convertToVO)
                .collect(Collectors.toList())
        );
    }

    /**
     * 获取B端登录用户信息
     * @return B端用户信息VO
     * @throws ServiceException 
     */
    @GetMapping("/login_info/get")
    public R<SysUserLoginVO> getLoginUser() throws ServiceException {
        return R.ok(sysUserService.getLoginUser().convertToVO());
    }

}
