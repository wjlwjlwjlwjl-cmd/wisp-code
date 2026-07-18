package com.nexus.nexusadminapi.config.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DictionaryTypeWriteReqDTO {
    @NotNull(message = "字典类型值不能为空")
    private String value;

    @NotNull(message = "字典类型键不能为空")
    private String typeKey;

    private String remark;  //选填字段
}
