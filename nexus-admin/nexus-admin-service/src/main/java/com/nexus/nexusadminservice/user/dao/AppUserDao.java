package com.nexus.nexusadminservice.user.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nexus.nexusadminapi.appuser.domain.dto.AppUserListReqDTO;
import com.nexus.nexusadminservice.user.domain.entity.AppUser;

import feign.Param;

@Mapper
public interface AppUserDao extends BaseMapper<AppUser>{
    /**
     * 根据邮箱获取用户信息
     * 
     * @param email     邮箱
     * @return          用户信息
     */
    AppUser selectByEmail(@Param("email") String email);

    /**
     * 查询总数
     * @param appUserListReqDTO 查询C端用户DTO
     * @return 用户总数
     */
    Long selectCount(AppUserListReqDTO appUserListReqDTO);

    /**
     * 分页查询C端用户
     * @param appUserListReqDTO 查询C端用户DTO
     * @return 用户列表
     */
    List<AppUser> selectPage(AppUserListReqDTO appUserListReqDTO);
}
