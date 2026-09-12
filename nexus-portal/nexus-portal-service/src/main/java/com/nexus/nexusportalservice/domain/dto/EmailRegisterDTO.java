package com.nexus.nexusportalservice.domain.dto;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.Max;

@Data
public class EmailRegisterDTO {
    @NotNull
    private String email;

    @NotNull
    private String username;

    private String password;
    private String code; //验证码
}
