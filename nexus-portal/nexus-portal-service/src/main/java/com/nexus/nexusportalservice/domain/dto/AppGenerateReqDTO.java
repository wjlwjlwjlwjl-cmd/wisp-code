package com.nexus.nexusportalservice.domain.dto;

import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class AppGenerateReqDTO {
    private Long appId;
    private String appDoc;
}
