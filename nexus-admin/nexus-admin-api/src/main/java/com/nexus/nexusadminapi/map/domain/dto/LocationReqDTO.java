package com.nexus.nexusadminapi.map.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 位置查询 DTO
 * 
 * LocationReqDTO
 */
@Data
public class LocationReqDTO {
    @NotNull(message = "纬度不能为空") //纬度
    private Double lat; 

    @NotNull(message= "经度不能为空") //经度
    private Double lng;

    /**
     * 返回格式化后的经纬度信息
     * 
     * @return  格式化后的经纬度信息
     */
    public String formatInfo(){
        return lat + "," + lng;
    }
}
