package com.nexus.nexusportalservice.service;

import com.nexus.nexusportalservice.domain.dto.AppGenerateRetDTO;

public interface IAppEditService {
    AppGenerateRetDTO appEdit(Long appId, String prompt) throws Exception;

    String vscodeAppEdit(String token, Long appId);

    Boolean confirmVscodeEdit(String token, String appId);
}
