package com.nexus.nexusportalservice.service;

import java.io.IOException;
import java.util.Map;

public interface ILocalFileStorage {
    public void store(Long appId, Map<String, String> files) throws IOException;
}
