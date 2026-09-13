package com.nexus.nexusportalservice.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("email_user")
public class EmailUser {
    @TableId(value="id", type=IdType.AUTO)
    private Long id;

    private String userId;

    private String username;

    private String email;

    private String password;
}
