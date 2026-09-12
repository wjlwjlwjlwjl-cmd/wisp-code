package com.nexus.nexusportalservice.controller;

import com.nexus.nexusportalservice.domain.dto.EmailLoginDTO;
import com.nexus.nexusportalservice.domain.dto.EmailRegisterDTO;
import com.nexus.nexusportalservice.domain.vo.EmailLoginVO;
import com.nexus.nexusportalservice.domain.vo.EmailRegisterVO;
import com.nexus.nexusportalservice.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wisp/user")
public class UserController {
    @Autowired
    private UserServiceImpl userService;

    /**
     *
     * @param emailRegisterDTO  username、password、email、code
     * @return JWT
     */
    @PostMapping("/register")
    public EmailRegisterVO register(@RequestBody EmailRegisterDTO emailRegisterDTO){
        return userService.emailRegister(emailRegisterDTO);
    }

    @GetMapping("/send_code")
    public Boolean sendCode(String email){
        return userService.sendCode(email);
    }

    /**
     *
     * @param emailLoginDTO email password
     * @return login result
     */
    @PostMapping("/login")
    public EmailLoginVO login(@RequestBody EmailLoginDTO emailLoginDTO){
        return userService.emailLogin(emailLoginDTO);
    }
}
