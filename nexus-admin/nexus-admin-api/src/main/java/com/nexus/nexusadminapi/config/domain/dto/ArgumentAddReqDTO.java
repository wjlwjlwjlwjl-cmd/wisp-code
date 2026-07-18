package com.nexus.nexusadminapi.config.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ArgumentAddReqDTO {
    @NotBlank(message = "参数名称不能为空")
    private String name;
    @NotBlank(message = "参数键不能为空")
    private String configKey;
    @NotBlank(message = "参数值不能为空")
    private String value;

    private String remark;
}
