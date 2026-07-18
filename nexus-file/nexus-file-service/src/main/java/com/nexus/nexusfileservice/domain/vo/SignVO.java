package com.nexus.nexusfileservice.domain.vo;

import lombok.Data;

@Data
public class SignVO {
    private String signature;

    private String host;

    private String pathPrefix;

    private String xOSSCredential;

    private String xOSSDate;

    private String policy;
    
}
