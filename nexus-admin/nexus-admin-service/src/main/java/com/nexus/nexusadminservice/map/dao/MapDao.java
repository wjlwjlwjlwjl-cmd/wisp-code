package com.nexus.nexusadminservice.map.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nexus.nexusadminservice.map.domain.entity.SysRegion;

@Mapper
public interface MapDao extends BaseMapper<SysRegion> {
    List<SysRegion> selectAllRegion();
}
