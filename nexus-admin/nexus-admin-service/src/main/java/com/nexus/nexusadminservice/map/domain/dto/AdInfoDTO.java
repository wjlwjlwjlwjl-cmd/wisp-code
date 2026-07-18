package com.nexus.nexusadminservice.map.domain.dto;

import lombok.Data;

/**
 * 逆地址解析结果（带详细地址，存储逆地址解析出的地点的具体信息）
 * 
 * AdInfoDTO
 */
@Data
public class AdInfoDTO {
    private String nation_code;         //国家代码
    private String adcode;              //行政区划代码
    private String city_code;           //城市代码
    private String name;                //行政区划名称
    private String nation;              //国家
    private String province;            //省、直辖市
    private String city;                //地级市
    private String district;            //县区一级
}
