package com.nexus.nexusportalservice.service;

import java.util.Map;

public interface IGiteeService {
    public void commit(Long appId, Map<String, String> files);
}
