package com.nexus.nexusadminapi.config.domain.vo;

import lombok.Data;

@Data
public class SysDictionaryTypeVO {
    private Long id;
    private String typeKey;
    private String value;
    private String remark;
    private Integer status;
}
