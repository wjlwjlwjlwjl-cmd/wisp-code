package com.nexus.nexusportalservice.domain.dto;

import com.nexus.nexusportalservice.domain.vo.AppVO;
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

    public AppVO convert2VO(){
        AppVO appVO = new AppVO();
        appVO.setAppDesc(this.appDesc);
        appVO.setId(this.id);
        appVO.setUserId(this.userId);
        appVO.setAppName(this.appName);
        appVO.setAppDoc(this.appDoc);
        appVO.setPreviewUrl(this.previewUrl);
        appVO.setAppType(this.appType);
        appVO.setDeploy(this.deploy);
        return appVO;
    }
}
