package com.nexus.nexusportalservice.domain.vo;

import com.nexus.nexusportalservice.domain.AppType;

import lombok.Data;

@Data
public class AppGenerateRetVO {
    private Long appId;         //appId
    private String previewUrl;  //预览 url
    private Integer appTypeNum;     //应用类型
}
