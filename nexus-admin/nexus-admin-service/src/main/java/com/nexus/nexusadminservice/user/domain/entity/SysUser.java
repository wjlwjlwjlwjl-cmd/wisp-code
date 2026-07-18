package com.nexus.nexusadminservice.user.domain.entity;

import com.nexus.nexuscommoncore.domain.entity.BaseDO;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统用户对象，数据库 sys_user 表 对应实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUser extends BaseDO {

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 手机号
     */
    private String phoneNumber;

    /**
     * 密码
     */
    private String password;

    /**
     * 身份
     */
    private String identity;

    /**
     * 状态
     */
    private String status;

    /**
     * 备注
     */
    private String remark;
}
