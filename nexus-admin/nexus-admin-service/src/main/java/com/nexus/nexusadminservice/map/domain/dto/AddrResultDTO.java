package com.nexus.nexusadminservice.map.domain.dto;

import lombok.Data;

/**
 * 用来存储具体的逆地址解析结果
 * 
 * AddrResultDTO
 */
@Data
public class AddrResultDTO {
    private String address;     //具体的详细地址
    private AdInfoDTO ad_info;  //城市地址详细信息
}
