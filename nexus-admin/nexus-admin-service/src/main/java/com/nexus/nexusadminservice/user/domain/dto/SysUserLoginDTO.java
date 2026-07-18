package com.nexus.nexusadminservice.user.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.beans.BeanUtils;

import com.nexus.nexusadminservice.user.domain.vo.SysUserLoginVO;
import com.nexus.nexuscommonsecurity.domain.dto.LoginUserDTO;

/**
 * B端登录用户信息DTO
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class SysUserLoginDTO extends LoginUserDTO {

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 身份
     */
    private String identity;

    /**
     * 状态
     */
    private String status;

    /**
     * B端用户登录信息DTO转VO
     * @return B端用户登录信息VO
     */
    public SysUserLoginVO convertToVO() {
        SysUserLoginVO sysUserLoginVO = new SysUserLoginVO();
        BeanUtils.copyProperties(this, sysUserLoginVO);
        return sysUserLoginVO;
    }
}
