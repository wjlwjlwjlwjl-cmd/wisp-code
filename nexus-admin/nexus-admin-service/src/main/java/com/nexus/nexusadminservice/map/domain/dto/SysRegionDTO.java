package com.nexus.nexusadminservice.map.domain.dto;

import lombok.Data;

@Data
public class SysRegionDTO {
    private Long id;            //区域 id
    private String name;        //区域名称
    private String fullName;    //区域全称
    private Long parentId;      //父级区域 id
    private String pinyin;      //拼音
    private Integer level;      //级别
    private Double longtitude;  //经度
    private Double latitude;    //纬度
}
