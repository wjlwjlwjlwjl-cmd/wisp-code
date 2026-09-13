package com.nexus.nexusportalservice.service;

import com.nexus.nexuscommoncore.domain.dto.BasePageDTO;
import com.nexus.nexusportalservice.domain.dto.AppDTO;
import com.nexus.nexusportalservice.domain.dto.ChatHistoryDTO;
import com.nexus.nexusportalservice.domain.dto.DeployAppDTO;
import com.nexus.nexusportalservice.domain.vo.AppVO;
import com.nexus.nexusportalservice.domain.vo.ChatHistoryVO;

import java.util.List;

public interface IAppBaseService {
    BasePageDTO<AppVO> listMyApp(String token, Integer current, Integer pageSize);

    BasePageDTO<AppVO> listDeployApp(String token, Integer current, Integer pageSize);

    DeployAppDTO appDeploy(String token, String appId, Boolean deploy);

    AppDTO appDetail(String appId);

    List<ChatHistoryDTO> appHistory(String appId);
}
