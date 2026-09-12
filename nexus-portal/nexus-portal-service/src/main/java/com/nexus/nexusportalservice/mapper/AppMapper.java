package com.nexus.nexusportalservice.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nexus.nexusportalservice.domain.entity.App;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AppMapper extends BaseMapper<App>{
    @Select("select count(1) from app where user_id=#{user_id}")
    public Integer getMyAppNum(String userId);
}
