package com.nexus.nexusportalservice.agent.controller;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.nexus.nexuscommondomain.domain.R;
import com.nexus.nexuscommondomain.exception.ServiceException;
import com.nexus.nexusportalservice.agent.node.MultiAgentWorkflow;
import com.nexus.nexusportalservice.domain.vo.AppGenerateRetVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/wisp/agent")
public class WispCodeAgentController {
    @Autowired
    MultiAgentWorkflow workflow;

    @PostMapping("/app/generate")
    public R<AppGenerateRetVO> agentAppGenerate(
            @RequestParam(value="appId") String appId,
            @RequestParam(value="appDoc") String appDoc
    ) throws ServiceException{
        Map<String, Object> initData = new HashMap<>();
        initData.put("appId", appId);
        initData.put("appDoc", appDoc);
        OverAllState overAllState = new OverAllState(initData);
        workflow.execute(overAllState);
        return R.ok(convert2TO(Long.valueOf(appId), overAllState));
    }

    private AppGenerateRetVO convert2TO(Long appId, OverAllState result) throws ServiceException{
        String status = result.value("status", String.class).orElse(null);
        if (!"SUCCESS".equals(status)) {
            String errorMsg = result.value("error", String.class).orElse(null);
            throw new ServiceException("工作流执行失败：" + errorMsg);
        }
        String appType = result.value("appType", String.class).orElse(null);
        String previewUrl = result.value("previewUrl", String.class).orElse(null);
        return new AppGenerateRetVO(appId, appType, previewUrl);
    }
}
