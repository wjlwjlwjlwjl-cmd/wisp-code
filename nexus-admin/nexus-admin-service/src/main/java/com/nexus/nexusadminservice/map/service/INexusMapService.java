package com.nexus.nexusadminservice.map.service;

import java.util.List;
import java.util.Map;

import com.nexus.nexusadminservice.map.domain.dto.SysRegionDTO;

public interface INexusMapService {
    public List<SysRegionDTO> get_list();                       // 获取城市列表
    public Map<String, List<SysRegionDTO>> get_py_list();       // 依据拼音获取城市列表
    public List<SysRegionDTO> get_child_list(int parentId);
}