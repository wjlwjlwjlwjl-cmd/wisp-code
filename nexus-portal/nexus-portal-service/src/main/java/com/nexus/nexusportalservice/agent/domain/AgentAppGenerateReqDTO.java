package com.nexus.nexusportalservice.agent.domain;

import lombok.Data;

/**
 * 多 Agent 应用生成请求体
 * 字段与类型和原 @RequestParam 完全一致：appId(String)、appDoc(String)
 */
@Data
public class AgentAppGenerateReqDTO {
    private String appId;
    private String appDoc;
}
