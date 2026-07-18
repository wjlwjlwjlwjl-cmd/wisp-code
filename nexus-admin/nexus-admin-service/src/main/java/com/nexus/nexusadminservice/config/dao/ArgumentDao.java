package com.nexus.nexusadminservice.config.dao;

import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nexus.nexusadminservice.config.domain.entity.SysArgument;

@Mapper
public interface ArgumentDao extends BaseMapper<SysArgument> {}