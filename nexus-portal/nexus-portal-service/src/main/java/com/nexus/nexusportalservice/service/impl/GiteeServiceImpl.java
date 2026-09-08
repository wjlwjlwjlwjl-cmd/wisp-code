package com.nexus.nexusportalservice.service.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexus.nexusportalservice.domain.dto.FileDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nexus.nexusportalservice.service.IGiteeService;

@Slf4j
@Service
public class GiteeServiceImpl implements IGiteeService{
    @Autowired
    private ChatClient chatClient;

    @Autowired
    private ChatMemory chatMemory;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Value("${gitee.user-code.owner}")
    private String userAppCodeOwner;

    @Value("${gitee.user-code.repo}")
    private String userAppCodeRepo;

    @Value("${gitee.user-code.branch}")
    private String userAppCodeBranch;

    /**
     *
     * @param appId 应用 id，用来区分仓库目录
     * @param appPath 应用代码目录
     * @param appType 应用类型
     * @param files 文件（fileName，fileContent）
     */
    @Override
    public void commit(Long appId, Path appPath, String appType, Map<String, String> files) throws Exception{
        List<FileDTO> fileDTOs = new ArrayList<>();
        for(String fileName: files.keySet()){
            Path filePath = appPath.resolve(fileName).toAbsolutePath().normalize();
            String fileContent = Files.readString(filePath);
            FileDTO fileDTO = new FileDTO();
            fileDTO.setFileContent(fileContent);
            fileDTO.setFilePath(appId + "/" + fileName);
            fileDTOs.add(fileDTO);
        }

        String filesJson = objectMapper.writeValueAsString(fileDTOs);
        String commitMessage = String.format("appId: %d, appType: %s", appId, appType);

        String systemPrompt = "你是一个代码提交助手，负责调用 commitFile 工具将代码提交到 Gitee 仓库。请严格按照用户提供的参数调用工具，不要添加任何解释。";
        String userPrompt = String.format(
                "请调用 commitFile 工具将代码提交到 Gitee 仓库。\n\n" +
                        "工具参数说明：\n" +
                        "- owner（仓库所有者）: \"%s\"\n" +
                        "- repo（仓库名称）: \"%s\"\n" +
                        "- branch（目标分支）: \"%s\"\n" +
                        "- message（提交信息）: \"%s\"\n" +
                        "- files（文件列表）: %s\n\n" +
                        "请直接调用 commitFile 工具，使用上述参数提交代码。不要添加任何解释或额外文本。",
                userAppCodeOwner, userAppCodeRepo, userAppCodeBranch, commitMessage, filesJson
        );

        String conversationId = String.valueOf(appId);
        String resp = chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
        log.info("comming result: {}", resp);
    }

    @Override
    public void pullUserAppCode(Long appId, Path userCodeBaseDir) {

    }

    @Override
    public void delete(Long appId) {

    }
}
