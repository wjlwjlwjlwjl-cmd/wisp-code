package com.nexus.nexusportalservice.domain.dto;

import lombok.Data;

@Data
public class AppDTO {
    private Long id;
    private String userId;
    private String appName;
    private String appDoc;
    private String appDesc;
    private String previewUrl;
    private String appType;
    private Boolean deploy;
}
