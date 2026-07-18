package com.nexus.nexusadminapi.config.domain.dto;

import com.nexus.nexuscommondomain.domain.dto.BasePageReqDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典数据列表DTO
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class DictionaryDataListReqDTO extends BasePageReqDTO {

    /**
     * 字典类型业务主键
     */
    @NotBlank(message = "字典类型业务主键不能为空")
    private String typeKey;

    /**
     * 字典数据值
     */
    private String value;
}
