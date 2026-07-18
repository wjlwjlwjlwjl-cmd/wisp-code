package com.nexus.nexusadminservice.user.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("app_user")
public class AppUser {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;                //主键
    private String nickName;        //昵称
    private String phoneNumber;     //电话号码
    private String email;           //邮箱
    private String avatar;          //头像二进制数据
}
