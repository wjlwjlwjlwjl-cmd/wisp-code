package com.nexus.nexusportalservice.domain.dto;

import com.nexus.nexuscommoncore.utils.BeanCopyUtil;
import com.nexus.nexusportalservice.domain.vo.AppGenerateRetVO;

import lombok.Data;

@Data
public class AppGenerateRetDTO {
    private Long appId;         //appId
    private String previewUrl;  //预览 url
    private Integer appTypeNum;     //应用类型

    public AppGenerateRetVO convertToVO(){
        AppGenerateRetVO appGenerateRetVO = new AppGenerateRetVO();
        BeanCopyUtil.copyProperties(this, appGenerateRetVO);
        return appGenerateRetVO;
    }
}
