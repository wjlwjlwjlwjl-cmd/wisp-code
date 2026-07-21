package com.nexus.nexusportalservice.service.impl;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.nexus.nexusportalservice.service.IGiteeService;

@Service
public class GiteeServiceImpl implements IGiteeService{

    @Override
    public void commit(Long appId, Map<String, String> files) {
    }
    
}
