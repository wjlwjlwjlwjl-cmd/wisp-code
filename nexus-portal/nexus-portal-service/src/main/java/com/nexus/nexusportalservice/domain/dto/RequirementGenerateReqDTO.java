package com.nexus.nexusportalservice.domain.dto;

import lombok.Data;

/**
 * 需求文档生成请求体
 * 字段与类型和原 @RequestParam 完全一致：input(String)
 */
@Data
public class RequirementGenerateReqDTO {
    private String input;
}
