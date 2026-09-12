package com.nexus.nexusportalservice.service;

import com.nexus.nexuscommoncore.domain.dto.BasePageDTO;
import com.nexus.nexusportalservice.domain.vo.AppVO;

public interface IAppBaseService {
    BasePageDTO<AppVO> listMyApp(String token, Integer current, Integer pageSize);

    BasePageDTO<AppVO> listDeployApp(String token, Integer current, Integer pageSize);

    Boolean appDeploy(String token, String appId);
}
