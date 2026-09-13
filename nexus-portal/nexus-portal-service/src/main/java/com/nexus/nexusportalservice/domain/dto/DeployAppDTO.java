package com.nexus.nexusportalservice.domain.dto;

import com.nexus.nexusportalservice.domain.vo.DeployAppVO;
import lombok.Data;

@Data
public class DeployAppDTO {
    private Boolean success;
    private String errMsg;

    public DeployAppVO convert2VO(){
        DeployAppVO deployAppVO = new DeployAppVO();
        deployAppVO.setErrMsg(this.errMsg);
        deployAppVO.setSuccess(this.success);
        return deployAppVO;
    }
}
