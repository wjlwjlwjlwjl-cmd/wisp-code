package com.nexus.nexusadminservice.map.domain.dto;

import lombok.Data;

/**
 * 逆地址解析结果（由经纬度获取具体的文字地点信息）
 * 
 * GeoResultDTO 
 */

@Data
public class GeoResultDTO {
    private AddrResultDTO result; //结果信息
}
