package com.nexus.nexusadminservice.config.domain.dto;

import lombok.Data;

@Data
public class SysDictionaryTypeDTO {
    private Long id;
    private String typeKey;
    private String value;
    private String remark;
    private Integer status;
}
