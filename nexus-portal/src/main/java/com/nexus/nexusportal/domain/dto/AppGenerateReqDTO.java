package com.nexus.nexusportal.domain.dto;

import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class AppGenerateReqDTO {
    private Long appId;
    private String appDoc;

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public String getAppDoc() {
        return appDoc;
    }

    public void setAppDoc(String appDoc) {
        this.appDoc = appDoc;
    }
}
