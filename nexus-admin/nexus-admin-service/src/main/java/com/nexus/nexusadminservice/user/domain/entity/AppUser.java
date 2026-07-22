package com.nexus.nexusadminservice.user.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nexus.nexuscommoncore.domain.entity.BaseDO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("app_user")
public class AppUser extends BaseDO {
    private String nickName;        //昵称
    private String phoneNumber;     //电话号码
    private String email;           //邮箱
    private String avatar;          //头像二进制数据
}
