package com.nexus.nexusportalservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.dockerjava.api.DockerClient;
import com.nexus.nexusportalservice.domain.AppType;
import com.nexus.nexusportalservice.domain.ModelParsedResult;
import com.nexus.nexusportalservice.domain.dto.AppGenerateRetDTO;
import com.nexus.nexusportalservice.domain.entity.App;
import com.nexus.nexusportalservice.enums.PreviewDeployPath;
import com.nexus.nexusportalservice.mapper.AppMapper;
import com.nexus.nexusportalservice.service.IAppEditService;
import com.nexus.nexusportalservice.utils.AppBuildUtil;
import com.nexus.nexusportalservice.utils.FileUtil;
import com.nexus.nexusportalservice.utils.GeneratedAppWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

@Slf4j
@Service
public class AppEditServiceImpl implements IAppEditService {
    private final ChatClient chatClient;
    private final GiteeServiceImpl giteeServiceImpl;
    private final LocalFileStorageImpl localFileStorageImpl;
    private final AppMapper appMapper;

    @Autowired
    DockerClient dockerClient;

    @Value("${app.preview.host}")
    String serverHost;
    @Value("${app.preview.container-name}")
    String containerName;

    public AppEditServiceImpl(ChatClient chatClient, GiteeServiceImpl giteeServiceImpl,
                                  LocalFileStorageImpl localFileStorageImpl, AppMapper appMapper) {
        this.chatClient = chatClient;
        this.giteeServiceImpl = giteeServiceImpl;
        this.localFileStorageImpl = localFileStorageImpl;
        this.appMapper = appMapper;
    }

    @Override
    public AppGenerateRetDTO appEdit(Long appId, String newPrompt) throws Exception{
        String systemPrompt = getSystemPrompt(appId);

        String currentPath = System.getProperty("user.dir");
        Path codePath = Path.of(currentPath, "user-code").resolve(String.valueOf(appId));
        Map<String, String> map = FileUtil.readAllFiles(codePath);
        String userPrompt = getUserPrompt(map, newPrompt);

        //1. 获取 LLM 生成的源代码
        String conversationId = String.valueOf(appId);
        String rawContent = chatClient.prompt()
                .system(systemPrompt)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .user(userPrompt)
                .call()
                .content();

        //2. 根据源代码获取应用类型
        ModelParsedResult.ParsedResult parsedResult = ModelParsedResult.parse(rawContent);
        Map<String, String> files = parsedResult.getFiles();
        String appType = GeneratedAppWriter.determineAppType(files);
        int appNum = AppType.getTypeNum(appType);

        //3. 把原先的代码删除
        //   整体代码，放到 user-code/${appId} 之中，处理后把需要预览
        //   将生成的预览内容，放到 user-preview 目录，在我们打包的docker容器中，就是 /workspace/portal
        //   同时，我们将这个目录挂载到 docker 主机，userapp-preview 容器，也挂在docker主机相同目录
        //   这样 nginx 容器就可以直接拿到内容进行展示了
        GeneratedAppWriter.deleteDirectory(codePath);
        Path appPath = GeneratedAppWriter.writeFiles(appId, files);
        handleApp(appId, appPath, appNum, PreviewDeployPath.PREVIEW.getPath());

        //previewUrl: appId/#（为了符合 Vue3 前端工程哈希路由模式，纯前端没有后端）
        String previewUrl = "http://" + serverHost + ":80" + "/preview/" + appId + "/#";

        //4. 更新数据库信息（应用类型、应用预览连接）
        appMapper.update(new LambdaUpdateWrapper<App>()
                .eq(App::getId, appId)
                .set(App::getAppType, appNum));
        appMapper.update(new LambdaUpdateWrapper<App>()
                .eq(App::getId, appId)
                .set(App::getPreviewUrl, previewUrl));

        //5. Gitee MCP，上传代码到 wispcode-gitee-repo 仓库，如果已经存在，那么就删除原有的内容
        giteeServiceImpl.commit(String.valueOf(appId), appPath, appType, files);

        AppGenerateRetDTO appGenerateRetDTO = new AppGenerateRetDTO();
        appGenerateRetDTO.setAppId(appId);
        appGenerateRetDTO.setAppType(appType);
        appGenerateRetDTO.setPreviewUrl(previewUrl);

        return appGenerateRetDTO;
    }

    private String getSystemPrompt(Long appId) {
        return String.join("\n",
                "你是资深全栈工程师和架构师，负责根据用户自然语言需求修改已有应用代码。",
                "### 修改规则",
                "1. 必须基于现有代码进行增量修改，不得重新生成整个应用。",
                "2. 只修改与用户需求相关的代码，尽量保持原有功能、结构和样式不变。",
                "3. 优先复用现有代码、组件和接口，禁止无关重构。",
                "4. 如果一个需求涉及多个文件，必须同时修改所有相关文件，确保功能完整。",
                "5. 修改后必须保证代码语法正确、引用正确，并保持项目原有技术栈和应用类型。",

                "### 应用类型",
                "应用类型仅允许以下三种：",
                "1. HTML：单个 index.html，使用 HTML、CSS 和原生 JavaScript。",
                "2. VUE3：Vue3 + Vite + Vue Router。",
                "3. VUE3_SPRING：Vue3 + Vite + Spring Boot。",
                "必须根据现有项目代码判断应用类型，不得随意改变应用类型。",
                "VUE3_SPRING 中，前端 API 必须使用 /" + appId + "/api 前缀，后端 Controller 必须以 /api 开头。",

                "### 输出格式约束 (CRITICAL)",
                "你必须严格按照以下格式输出，解析器依赖此格式：",
                "1. **第一行**: 必须输出应用类型（APP_TYPE=HTML、APP_TYPE=VUE3、APP_TYPE=VUE3_SPRING）。",
                "2. **文件内容**: 紧接着按以下格式输出项目的全部文件，包括修改和未修改的文件：",
                "FILE: <relative_path>",
                "```<language>",
                "<complete_file_content>",
                "```",
                "   - `<relative_path>`: 文件的相对路径。",
                "   - `<complete_file_content>`: **完整的文件内容**，绝对禁止省略、使用占位符或 `// ...`。",
                "3. 必须输出当前项目的全部文件，未修改的文件也必须完整输出。",
                "4. 不要输出任何解释、总结或其它文字。"
        );
    }

    private String getUserPrompt(Map<String, String> codeFiles, String newPrompt) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("对应用代码进行修改，要求：\n\n");
        prompt.append(newPrompt);

        prompt.append("以下是完整代码文件，请只对目标元素进行最小必要修改：\n\n");

        for (Map.Entry<String, String> entry : codeFiles.entrySet()) {
            prompt.append("FILE: ").append(entry.getKey()).append("\n");
            prompt.append("```html\n");
            prompt.append(entry.getValue());
            prompt.append("\n```\n\n");
        }

        return prompt.toString();
    }

    private void handleApp(Long appId, Path appPath, int appNum, String previewDeployPath){
        if(appNum == 0){
            try{
                Path targetFile = FileUtil.ensureAppDir(appId, "user-preview").resolve("dist");
                FileUtil.copyDirectory(appPath, targetFile);
            }
            catch(IOException e){
                System.out.println(e.getStackTrace());
            }
        }
        else if(appNum == 1){
            AppBuildUtil.buildVuePro(appId, appPath, previewDeployPath);
        }
        else if(appNum == 2){
            //部署前端
            Path appFrontendPath = appPath.resolve("frontend");
            AppBuildUtil.buildVuePro(appId, appFrontendPath, previewDeployPath);

            //部署后端
            Path springBootDir = appPath.resolve("backend");
            AppBuildUtil.buildSpringBoot(appId, springBootDir, dockerClient, previewDeployPath, containerName);
        }
    }
}
