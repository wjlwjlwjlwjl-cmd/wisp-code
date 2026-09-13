package com.nexus.nexusportalservice.controller;

import com.nexus.nexuscommondomain.domain.R;
import com.nexus.nexusportalservice.domain.dto.EmailLoginDTO;
import com.nexus.nexusportalservice.domain.dto.EmailRegisterDTO;
import com.nexus.nexusportalservice.domain.vo.EmailLoginVO;
import com.nexus.nexusportalservice.domain.vo.EmailRegisterVO;
import com.nexus.nexusportalservice.domain.vo.UserVO;
import com.nexus.nexusportalservice.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wisp/user")
public class UserController {
    @Autowired
    private UserServiceImpl userService;

    /**
     * 邮箱注册
     *
     * @param emailRegisterDTO  username、password、email、code
     * @return 统一响应，data 为注册结果（success、errMsg）
     */
    @PostMapping("/register")
    public R<EmailRegisterVO> register(@RequestBody EmailRegisterDTO emailRegisterDTO) {
        return R.ok(userService.emailRegister(emailRegisterDTO));
    }

    /**
     * 发送邮箱验证码
     *
     * @param email 邮箱
     * @return 统一响应，data 为是否发送成功
     */
    @GetMapping("/send_code")
    public R<Boolean> sendCode(String email) {
        return R.ok(userService.sendCode(email));
    }

    /**
     * 获取当前登录用户信息（后端自动解析 JWT）
     *
     * @param token Authorization 头携带的 JWT
     * @return 统一响应，data 为用户信息
     */
    @GetMapping("/get_info")
    public R<UserVO> getInfo(@RequestHeader(value = "Authorization") String token) {
        return R.ok(userService.getInfo(token));
    }

    /**
     * 邮箱登录
     *
     * @param emailLoginDTO email、password
     * @return 统一响应，data 为登录结果（accessToken、expires）
     */
    @PostMapping("/login")
    public R<EmailLoginVO> login(@RequestBody EmailLoginDTO emailLoginDTO) {
        return R.ok(userService.emailLogin(emailLoginDTO));
    }
}
