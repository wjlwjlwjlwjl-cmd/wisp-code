package com.nexus.nexusportal.domain.vo;

import com.nexus.nexusportal.domain.AppType;

import lombok.Data;

@Data
public class AppGenerateRetVO {
    private Long appId;         //appId
    private String previewUrl;  //预览 url
    private AppType appType;     //应用类型
}
