package com.nexus.nexusportalservice.service.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.nexus.nexusportalservice.domain.dto.RequirementDTO;
import com.nexus.nexusportalservice.domain.entity.App;
import com.nexus.nexusportalservice.mapper.AppMapper;
import com.nexus.nexusportalservice.service.IRequirementService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@SuppressWarnings({ "null" })
public class RequirementServiceImpl implements IRequirementService {
    private final ChatClient chatClient;
    private final AppMapper appMapper;

    public RequirementServiceImpl(ChatClient chatClient, AppMapper appMapper) {
        this.chatClient = chatClient;
        this.appMapper = appMapper;
    }

    /**
     * 生成应用需求文档
     * 
     * @param 用户初始描述
     * @return 文档生成与解析结果
     */
    @Override
    public RequirementDTO requirementGenerate(String input) {
        String systemPrompt = composeSystemPrompt();
        String rawMarkDown = chatClient.prompt()
            .system(systemPrompt) 
            .user(input)
            .call()
            .content();

        String appName = parseSection(rawMarkDown, 1, "应用名称");
        String appDesc = parseSection(rawMarkDown, 2, "应用描述");
        Long userId = 1L; // todo

        App app = new App();
        app.setUserId(userId);
        app.setAppName(appName);
        app.setAppDoc(rawMarkDown);
        app.setAppDesc(appDesc);
        appMapper.insert(app);

        RequirementDTO requirementDTO = new RequirementDTO();
        requirementDTO.setAppId(app.getId());
        requirementDTO.setContent(rawMarkDown);

        return requirementDTO;
    }

    /**
     * 产生系统提示词(开发文档生成)
     * 
     * @return  系统提示词
     */
    private String composeSystemPrompt() {
        return String.join("\n",
                "你是资深产品经理。根据⽤⼾提供的需求,⽣成正式且简洁的应⽤需求⽂档.请使⽤Markdown 严格排版，采⽤如下结构与编号：",
                "# 应⽤需求⽂档",
                "## 1. 应⽤名称",
                "## 2. 应⽤描述",
                "## 3. 应⽤核⼼功能",
                "核⼼功能采⽤ 3.1、3.2… 的编号格式分点呈现；若⽤⼾已明确输⼊核⼼功能，完全以⽤⼾输⼊内容为准；若⽤⼾未提及核⼼功能，结合⽤⼾输⼊内容⽣成不超过两个极简功能，所有功能均为列表类功能，所有功能均独⽴实现，不依赖任何第三⽅服务。需求⽂档中不提及任何第三⽅服务或平台。",
                "注意：仅输出符合以上要求的需求⽂档正⽂，不要任何的附加说明或多余内容");
    }

    /**
     * 根据 LLM 生成内容解析内容
     * 
     * @param content       LLM 生成内容
     * @param sectionNumber 章节的序号
     * @param sectionName   章节名称
     * @return 解析出的该章节的内容
     */
    private String parseSection(String content, int sectionNumber, String sectionName) {
        String regex = String.format("##\\s*%d\\.\\s*%s\\s*[\\r\\n]+([^#]+?)(?=##|$)", sectionNumber, sectionName);
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        log.warn("未能从内容中解析出{}", sectionName);
        return "";
    }
}
