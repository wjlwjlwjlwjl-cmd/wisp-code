package com.nexus.nexusportalservice.service;

import com.nexus.nexuscommoncore.domain.dto.BasePageDTO;
import com.nexus.nexusportalservice.domain.dto.DeployAppDTO;
import com.nexus.nexusportalservice.domain.vo.AppVO;

public interface IAppBaseService {
    BasePageDTO<AppVO> listMyApp(String token, Integer current, Integer pageSize);

    BasePageDTO<AppVO> listDeployApp(String token, Integer current, Integer pageSize);

    DeployAppDTO appDeploy(String token, String appId, Boolean deploy);
}
