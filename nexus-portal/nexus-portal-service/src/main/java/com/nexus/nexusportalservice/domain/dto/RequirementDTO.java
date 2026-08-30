package com.nexus.nexusportalservice.domain.dto;

import com.nexus.nexuscommoncore.utils.BeanCopyUtil;
import com.nexus.nexusportalservice.domain.vo.RequirementVO;

import lombok.Data;

@Data
public class RequirementDTO {
    private Long appId; 
    private String content;

    public RequirementVO convertToVO(){
        RequirementVO requirementVO = new RequirementVO();
        BeanCopyUtil.copyProperties(this, requirementVO);
        return requirementVO;
    }
}
