package com.nexus.nexusportalservice.service;

import java.nio.file.Path;
import java.util.Map;

public interface IGiteeService {
    void commit(String appId, Path appPath, String appType, Map<String, String> files) throws Exception;

    void pullUserAppCode(Long appId, Path userCodeBaseDir) throws Exception;

    //删除 wispcode-gitee-repo/${appId} 下的代码
    void delete(String appId);
}
