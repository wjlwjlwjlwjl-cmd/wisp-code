package com.nexus.nexusportalservice.service;

import com.nexus.nexusportalservice.domain.dto.AppGenerateRetDTO;

public interface IAppGenerateService {
    AppGenerateRetDTO appGenerate(Long appId, String prompt);
}
