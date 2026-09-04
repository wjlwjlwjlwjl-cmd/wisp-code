package com.nexus.nexusportalservice.service.impl;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.nexus.nexusportalservice.utils.GeneratedAppWriter;
import com.nexus.nexusportalservice.service.ILocalFileStorage;

@Service
public class LocalFileStorageImpl implements ILocalFileStorage{
    @Override
    public void store(Long appId, Map<String, String> files) throws IOException {
        GeneratedAppWriter.writeFiles(appId, files);
    }
}
