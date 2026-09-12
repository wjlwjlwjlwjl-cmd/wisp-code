package com.nexus.nexusportalservice.service;

import com.nexus.nexusportalservice.domain.dto.EmailLoginDTO;
import com.nexus.nexusportalservice.domain.dto.EmailRegisterDTO;
import com.nexus.nexusportalservice.domain.vo.EmailLoginVO;
import com.nexus.nexusportalservice.domain.vo.EmailRegisterVO;

public interface IUserService {
    public EmailRegisterVO emailRegister(EmailRegisterDTO emailRegisterDTO);

    public boolean sendCode(String email);

    public EmailLoginVO emailLogin(EmailLoginDTO emailLoginDTO);
}
