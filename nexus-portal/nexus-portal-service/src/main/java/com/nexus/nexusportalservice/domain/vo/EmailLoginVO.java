package com.nexus.nexusportalservice.domain.vo;

import lombok.Data;

@Data
public class EmailLoginVO {
    //如果登陆成功返回 JWT
    private String accessToken; //JWT

    private Long expires;
}
