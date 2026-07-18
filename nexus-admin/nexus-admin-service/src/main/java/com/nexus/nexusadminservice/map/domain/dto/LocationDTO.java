package com.nexus.nexusadminservice.map.domain.dto;

import lombok.Data;

/**
 * 包装出来经纬度，供给 PoiDTO 表示经纬度信息
 * 
 * LocationDTO
 */
@Data
public class LocationDTO {
    private Double lat;         //纬度
    private Double lng;         //经度

    /**
     * 
     * @return   格式化的经纬度信息
     */
    public String formatInfo(){ 
        return lat + "," + lng;
    }
}
