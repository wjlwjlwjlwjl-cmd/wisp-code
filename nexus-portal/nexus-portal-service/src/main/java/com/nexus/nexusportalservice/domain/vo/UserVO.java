package com.nexus.nexusportalservice.domain.vo;

import lombok.Data;

@Data
public class UserVO {
    /**
     * 用户id
     */
    private String userId;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户名称
     */
    private String username;
}
