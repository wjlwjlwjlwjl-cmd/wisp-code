package com.nexus.nexusportalservice.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nexus.nexusportalservice.domain.entity.App;

@Mapper
public interface AppMapper extends BaseMapper<App>{
    
}
