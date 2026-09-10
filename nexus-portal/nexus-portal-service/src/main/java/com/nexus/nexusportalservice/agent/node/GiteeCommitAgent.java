package com.nexus.nexusportalservice.agent.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.nexus.nexuscommondomain.exception.ServiceException;
import com.nexus.nexusportalservice.service.impl.GiteeServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class GiteeCommitAgent implements NodeAction {
    private GiteeServiceImpl giteeService;

    public GiteeCommitAgent(GiteeServiceImpl giteeService){
        this.giteeService = giteeService;
    }

    //giteeServiceImpl.commit(appId, appPath, appType, files);
    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        Map<String, Object> ret = new HashMap<>();

        Boolean appGenerated = state.value("appGenerated", Boolean.class).orElse(null);
        if(!appGenerated){
            throw new ServiceException("应用生成失败");
        }

        try{
            Map<String, String> filesToCommit = state.value("files", Map.class).orElse(null);
            String appId = state.value("appId", String.class).orElse(null);
            Path appPath = state.value("appPath", Path.class).orElse(null);
            String appType = state.value("appType", String.class).orElse(null);

            //如果能够获取到先删除旧有文件，再创建新文件
            giteeService.commit(appId, appPath, appType, filesToCommit);
            ret.put("codeCommit", true);
            ret.put("status", "SUCCESS");

            System.out.println("Gitee Commit Success");
        }
        catch(Exception e){
            ret.put("codeCommit", false);
            ret.put("status", "FAILED");
            ret.put("error", e.getMessage());

            log.warn("Gitee Commit Failed: {}", e.getMessage());
        }
        return ret;
    }
}
