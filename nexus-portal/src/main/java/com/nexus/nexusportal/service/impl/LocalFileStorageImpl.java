package com.nexus.nexusportal.service.impl;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.nexus.nexusportal.domain.utils.LocalFileUtil;
import com.nexus.nexusportal.service.ILocalFileStorage;

@Service
public class LocalFileStorageImpl implements ILocalFileStorage{
    @Override
    public void store(Long appId, Map<String, String> files) throws IOException {
        LocalFileUtil.writeFiles(appId, files);
    }
}
