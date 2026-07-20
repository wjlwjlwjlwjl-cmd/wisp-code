package com.nexus.nexusportal.service;

import com.nexus.nexusportal.domain.dto.AppGenerateRetDTO;

public interface IAppGenerateService {
    AppGenerateRetDTO appGenerate(Long appId, String prompt);
}
