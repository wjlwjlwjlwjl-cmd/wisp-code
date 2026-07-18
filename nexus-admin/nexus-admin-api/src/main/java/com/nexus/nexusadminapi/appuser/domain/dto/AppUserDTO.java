package com.nexus.nexusadminapi.appuser.domain.dto;

import lombok.Data;
import org.springframework.beans.BeanUtils;

import com.nexus.nexusadminapi.appuser.domain.vo.AppUserVo;

import java.io.Serializable;

/**
 * C端用户DTO
 */
@Data
public class AppUserDTO implements Serializable {

    /**
     * C端用户ID
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 手机号
     */
    private String phoneNumber;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * DTO对象转换VO对象
     * @return VO对象
     */
    public AppUserVo convertToVO() {
        AppUserVo appUserVo = new AppUserVo();
        BeanUtils.copyProperties(this, appUserVo);
        return appUserVo;
    }
}
