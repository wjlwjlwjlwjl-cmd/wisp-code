package com.nexus.nexusportal.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nexus.nexusportal.domain.entity.App;

@Mapper
public interface AppMapper extends BaseMapper<App>{
    
}
