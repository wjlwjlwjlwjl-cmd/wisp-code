package com.nexus.nexusportalservice.agent.controller;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.nexus.nexuscommondomain.domain.R;
import com.nexus.nexuscommondomain.exception.ServiceException;
import com.nexus.nexusportalservice.agent.domain.AgentAppGenerateReqDTO;
import com.nexus.nexusportalservice.agent.node.MultiAgentWorkflow;
import com.nexus.nexusportalservice.domain.vo.AppGenerateRetVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
            @RequestBody AgentAppGenerateReqDTO req
    ) throws Exception{
        Map<String, Object> initData = new HashMap<>();
        initData.put("appId", req.getAppId());
        initData.put("appDoc", req.getAppDoc());
        OverAllState overAllState = new OverAllState(initData);
        OverAllState result = workflow.execute(overAllState);
        return R.ok(convert2TO(Long.valueOf(req.getAppId()), result));
    }

    private AppGenerateRetVO convert2TO(Long appId, OverAllState result) throws ServiceException{
        String status = result.value("status", String.class).orElse(null);
        if (!"SUCCESS".equals(status)) {
            String errorMsg = result.value("error", String.class).orElse(null);
            throw new ServiceException("【" + status + "】工作流执行失败：" + errorMsg);
        }
        String appType = result.value("appType", String.class).orElse(null);
        String previewUrl = result.value("previewUrl", String.class).orElse(null);
        return new AppGenerateRetVO(appId, previewUrl, appType);
    }
}
