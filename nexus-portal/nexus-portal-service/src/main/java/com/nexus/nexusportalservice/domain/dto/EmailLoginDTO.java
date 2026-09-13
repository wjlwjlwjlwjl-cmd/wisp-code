package com.nexus.nexusportalservice.domain.dto;

import lombok.Data;

@Data
public class EmailLoginDTO {
    //输入注册时的邮箱，辅助密码，完成登录
    private String email;
    private String password;
}
