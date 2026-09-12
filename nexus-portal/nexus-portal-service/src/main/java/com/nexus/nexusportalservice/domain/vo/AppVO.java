package com.nexus.nexusportalservice.domain.vo;

import lombok.Data;

@Data
public class AppVO {
    private Long id;
    private String userId;
    private String appName;
    private String appDoc;
    private String appDesc;
    private String previewUrl;
    private String appType;
    private Boolean deploy;
}
