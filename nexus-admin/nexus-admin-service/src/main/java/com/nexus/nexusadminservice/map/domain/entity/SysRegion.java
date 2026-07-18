package com.nexus.nexusadminservice.map.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * sys_region 表对应实体类
 * 
 * SysRegion
 */

@TableName("sys_region")
@Data
public class SysRegion{
    private Long id;            //区域 id
    private String name;        //区域名称
    private String fullName;    //区域全称
    private Long parentId;      //父级区域 id
    private String pinyin;      //拼音
    private Integer level;      //级别
    private Double longtitude;  //经度
    private Double latitude;    //纬度
    private String code;        //区域编码
    private String parentCode;  //父级区域编码
}
