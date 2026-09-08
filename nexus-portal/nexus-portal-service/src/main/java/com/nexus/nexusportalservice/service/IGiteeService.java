package com.nexus.nexusportalservice.service;

import java.nio.file.Path;
import java.util.Map;

public interface IGiteeService {
    void commit(Long appId, Path appPath, String appType, Map<String, String> files) throws Exception;

    void pullUserAppCode(Long appId, Path userCodeBaseDir) throws Exception;

    void delete(Long appId) throws Exception;
}
