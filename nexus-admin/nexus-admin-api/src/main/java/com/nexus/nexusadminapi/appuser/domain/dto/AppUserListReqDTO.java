package com.nexus.nexusadminapi.appuser.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

import com.nexus.nexuscommondomain.domain.dto.BasePageReqDTO;

/**
 * 查询C端用户DTO
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class AppUserListReqDTO extends BasePageReqDTO  implements Serializable {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 手机号
     */
    private String phoneNumber;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 邮箱
     */
    private String email;
}
