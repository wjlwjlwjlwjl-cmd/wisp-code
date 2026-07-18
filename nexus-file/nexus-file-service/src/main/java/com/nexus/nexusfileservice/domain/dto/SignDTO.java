package com.nexus.nexusfileservice.domain.dto;

import lombok.Data;

/**
 * SignDTO
 */
@Data
public class SignDTO {
    private String signature;

    private String host;

    private String pathPrefix;

    private String xOSSCredential;

    private String xOSSDate;

    private String policy;
}
