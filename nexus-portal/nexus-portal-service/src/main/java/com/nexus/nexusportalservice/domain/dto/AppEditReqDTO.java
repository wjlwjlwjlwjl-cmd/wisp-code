package com.nexus.nexusportalservice.domain.dto;

import lombok.Data;

/**
 * 应用编辑请求体
 * 字段与类型和原 @RequestParam 完全一致：appId(Long)、newPrompt(String)
 */
@Data
public class AppEditReqDTO {
    private Long appId;
    private String newPrompt;
}
