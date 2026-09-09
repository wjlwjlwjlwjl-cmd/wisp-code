package com.nexus.nexusportalservice.agent.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ErrorFixingAgent implements NodeAction {
    private ChatClient chatClient;

    public ErrorFixingAgent(ChatClient chatClient){
        this.chatClient = chatClient;
    }

    /**
     *
     * @param state 传入： appId appDoc appType error errorType files
     * @return 传出 fixSuccess
     * @throws Exception
     */
    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        Map<String, Object> ret = new HashMap<>();

        return ret;
    }
}
