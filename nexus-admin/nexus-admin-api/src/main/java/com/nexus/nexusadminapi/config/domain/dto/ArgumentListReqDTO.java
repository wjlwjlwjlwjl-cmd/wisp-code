package com.nexus.nexusadminapi.config.domain.dto;

import com.nexus.nexuscommondomain.domain.dto.BasePageReqDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查看参数DTO
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class ArgumentListReqDTO extends BasePageReqDTO {

    /**
     * 参数名称
     */
    private String name;

    /**
     * 参数业务主键
     */
    private String configKey;
}
