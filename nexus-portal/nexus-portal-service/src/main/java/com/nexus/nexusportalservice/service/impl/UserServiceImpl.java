package com.nexus.nexusportalservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nexus.nexuscommondomain.domain.dto.LoginUserDTO;
import com.nexus.nexuscommondomain.domain.dto.TokenDTO;
import com.nexus.nexuscommonredis.service.RedisService;
import com.nexus.nexuscommonsecurity.service.TokenService;
import com.nexus.nexusportalservice.domain.dto.EmailLoginDTO;
import com.nexus.nexusportalservice.domain.dto.EmailRegisterDTO;
import com.nexus.nexusportalservice.domain.entity.EmailUser;
import com.nexus.nexusportalservice.domain.vo.EmailLoginVO;
import com.nexus.nexusportalservice.domain.vo.EmailRegisterVO;
import com.nexus.nexusportalservice.domain.vo.UserVO;
import com.nexus.nexusportalservice.mapper.EmailUserMapper;
import com.nexus.nexusportalservice.service.IUserService;
import com.nexus.nexuscommonmessage.service.EmailService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private RedisService redisService; //用来存放验证码，并在这里维护过期时间

    @Autowired
    private EmailService emailService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmailUserMapper emailUserMapper;

    private final SecureRandom secureRandom = new SecureRandom();
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @Override
    public EmailRegisterVO emailRegister(EmailRegisterDTO emailRegisterDTO) {
        EmailRegisterVO emailRegisterVO = new EmailRegisterVO();

        String username = emailRegisterDTO.getUsername();
        String password = emailRegisterDTO.getPassword();
        String email = emailRegisterDTO.getEmail();
        String code = emailRegisterDTO.getCode();

        //去查看一下，如果已经数据库中已经注册，那么拒绝重复注册
        EmailUser emailUser = emailUserMapper.selectOne(new LambdaQueryWrapper<EmailUser>()
                .eq(EmailUser::getEmail, email)
                .or()
                .eq(EmailUser::getUsername, username)
        );
        if(emailUser != null){
            emailRegisterVO.setSuccess(false);
            emailRegisterVO.setErrMsg("邮箱已注册，或者用户名已存在");
            return emailRegisterVO;
        }

        //将验证码和 Redis 中存储的验证码进行比较
        String retCode = redisService.getCacheObject(email, String.class);
        if(retCode == null || !retCode.equals(code)){
            emailRegisterVO.setSuccess(false);
            emailRegisterVO.setErrMsg("验证码非法或已过期");
            return emailRegisterVO;
        }

        //比较通过，更新用户信息
        String userId = "WispUser-" + UUID.randomUUID().toString();

        emailUser = new EmailUser();
        emailUser.setEmail(email);
        emailUser.setPassword(password);
        emailUser.setUsername(username);
        emailUser.setUserId(userId);

        emailUserMapper.insert(emailUser);

        emailRegisterVO.setSuccess(true);
        emailRegisterVO.setErrMsg(null);
        return emailRegisterVO;
    }

    @Override
    public boolean sendCode(String email) {
        if(!checkEmail(email)){
            return false;
        }

        int raw = secureRandom.nextInt(1_000_000);
        String code = String.format("%06d", raw);

        //将验证码发送到 redis 服务器中，并设置 ttl 为 5min（如果有了 email 的验证码，拒绝发送）
        if(redisService.hasKey(email)){
            return false;
        }
        redisService.setCacheObject(email, code, 5, TimeUnit.MINUTES);

        return emailService.sendSimpleEmail(email, code);
    }

    @Override
    public EmailLoginVO emailLogin(EmailLoginDTO emailLoginDTO) {
        EmailLoginVO emailLoginVO = new EmailLoginVO();
        String email = emailLoginDTO.getEmail();
        String password = emailLoginDTO.getPassword();

        EmailUser emailUser = emailUserMapper.selectOne(new LambdaQueryWrapper<EmailUser>()
                .eq(EmailUser::getEmail, email)
                .eq(EmailUser::getPassword, password)
        );
        if(emailUser == null){
            emailLoginVO.setAccessToken(null);
            return emailLoginVO;
        }
        String userId = emailUser.getUserId();
        String userName = emailUser.getUsername();

        LoginUserDTO loginUserDTO = new LoginUserDTO();
        loginUserDTO.setUserId(userId);
        loginUserDTO.setEmail(email);
        loginUserDTO.setUsername(userName);

        TokenDTO tokenDTO = tokenService.createToken(loginUserDTO);
        BeanUtils.copyProperties(tokenDTO, emailLoginVO);

        System.out.printf("返回Accesskey: %s\n", emailLoginVO.getAccessToken());

        return emailLoginVO;
    }

    @Override
    public UserVO getInfo(String token) {
        LoginUserDTO loginUserDTO = tokenService.getLoginUser(token);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(loginUserDTO, userVO);
        System.out.printf("%s %s %s", userVO.getUsername(), userVO.getUserId(), userVO.getEmail());
        return userVO;
    }

    private boolean checkEmail(String email){
        if(email == null || email.isBlank())    return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }
}
