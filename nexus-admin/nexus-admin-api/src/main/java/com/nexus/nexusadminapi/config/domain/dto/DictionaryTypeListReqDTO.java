package com.nexus.nexusadminapi.config.domain.dto;

import com.nexus.nexuscommondomain.domain.dto.BasePageReqDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class DictionaryTypeListReqDTO extends BasePageReqDTO {
    private String value;       //字典类型值
    private String typeKey;     //字典类型键
}
