package com.nexus.nexusadminservice.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nexus.nexusadminservice.user.domain.entity.SysUser;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 处理sys_user表
 */
@Mapper
public interface SysUserDao extends BaseMapper<SysUser> {

    SysUser selectByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    List<SysUser> selectList(SysUser sysUser);
}
