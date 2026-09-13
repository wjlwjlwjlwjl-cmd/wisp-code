package com.nexus.nexusportalservice.domain.vo;

import com.nexus.nexusportalservice.domain.AppType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppGenerateRetVO {
    private Long appId;         //appId
    private String previewUrl;  //预览 url
    private String appType;     //应用类型
}
