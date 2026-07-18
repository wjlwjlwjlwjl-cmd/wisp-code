package com.nexus.nexusfileservice.service.impl;

import org.springframework.web.multipart.MultipartFile;

import com.nexus.nexusfileservice.domain.dto.FileDTO;
import com.nexus.nexusfileservice.domain.dto.SignDTO;

public interface IFileService {
    FileDTO upload(MultipartFile file);
    SignDTO getSign();
}
