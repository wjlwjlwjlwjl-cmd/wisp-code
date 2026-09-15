package com.nexus.nexusportalservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.AuthCmd;
import com.nexus.nexuscommondomain.constants.SecurityConstants;
import com.nexus.nexuscommondomain.constants.TokenConstants;
import com.nexus.nexuscommondomain.domain.dto.LoginUserDTO;
import com.nexus.nexuscommonredis.service.RedisService;
import com.nexus.nexuscommonsecurity.service.TokenService;
import com.nexus.nexusportalservice.domain.AppType;
import com.nexus.nexusportalservice.domain.ModelParsedResult;
import com.nexus.nexusportalservice.domain.dto.AppGenerateRetDTO;
import com.nexus.nexusportalservice.domain.dto.FileDTO;
import com.nexus.nexusportalservice.domain.entity.App;
import com.nexus.nexusportalservice.enums.PreviewDeployPath;
import com.nexus.nexusportalservice.mapper.AppMapper;
import com.nexus.nexusportalservice.service.IAppEditService;
import com.nexus.nexusportalservice.utils.AppBuildUtil;
import com.nexus.nexusportalservice.utils.FileUtil;
import com.nexus.nexusportalservice.utils.GeneratedAppWriter;
import com.nexus.nexusportalservice.utils.GiteeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AppEditServiceImpl implements IAppEditService {
    private final ChatClient chatClient;
    private final GiteeServiceImpl giteeServiceImpl;
    private final LocalFileStorageImpl localFileStorageImpl;
    private final AppMapper appMapper;

    @Autowired
    private DockerClient dockerClient;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private GiteeUtil giteeUtil;
    @Autowired
    private RedisService redisService;

    @Value("${app.preview.host}")
    private String serverHost;
    @Value("${app.preview.container-name}")
    private String containerName;
    @Value("${code.host}")
    private String codeHost;
    @Value("${code.port}")
    private String codePort;
    @Value("${gitee.user-code.owner}")
    private String giteeOwner;
    @Value("${gitee.user-code.repo}")
    private String giteeRepo;
    @Value("${gitee.user-code.branch}")
    private String giteeBranch;

    private final String vscodeUrlTemplate = "http://%s:%s/?folder=/home/workspace/%s";

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
        AppBuildUtil.handleApp(appId, appPath, appNum, PreviewDeployPath.PREVIEW.getPath(), dockerClient, containerName);

        //previewUrl: appId/#（为了符合 Vue3 前端工程哈希路由模式，纯前端没有后端）
        String previewUrl = "http://" + serverHost + ":80" + "/preview/" + appId + "/#";

        //4. 更新数据库信息（应用类型、应用预览连接）
        appMapper.update(new LambdaUpdateWrapper<App>()
                .eq(App::getId, appId)
                .set(App::getAppType, appNum));
        appMapper.update(new LambdaUpdateWrapper<App>()
                .eq(App::getId, appId)
                .set(App::getPreviewUrl, previewUrl));

        //5. 上传代码到 wispcode-gitee-repo 仓库（直接调用 Gitee API）。
        //   非致命：应用已修改入库、预览就绪，Gitee 备份失败只记录日志，不让整体请求 500。
        try {
            giteeServiceImpl.commit(String.valueOf(appId), appPath, appType, files);
        } catch (Exception e) {
            log.warn("Gitee commit 失败（已忽略，应用修改已生成成功）：appId={}, err={}", appId, e.getMessage());
        }

        AppGenerateRetDTO appGenerateRetDTO = new AppGenerateRetDTO();
        appGenerateRetDTO.setAppId(appId);
        appGenerateRetDTO.setAppType(appType);
        appGenerateRetDTO.setPreviewUrl(previewUrl);

        return appGenerateRetDTO;
    }

    @Override
    public String vscodeAppEdit(String token, Long appId) {
        LoginUserDTO loginUserDTO = tokenService.getLoginUser(token);
        String userId = loginUserDTO.getUserId();
        /*App app = appMapper.selectOne(new LambdaQueryWrapper<App>()
                .eq(App::getUserId, userId)
                .eq(App::getId, appId)
        );
        if(app == null){
            log.warn("用户{}没有编辑权限", userId);
            return null; //没有编辑权限
        }*/
        if(!redisService.hasKey(TokenConstants.LOGIN_TOKEN_KEY + userId)){
            log.warn("用户{}没有编辑权限", userId);
            return null; //没有编辑权限
        }

        String currentPath = System.getProperty("user.dir");
        Path appPath = Path.of(currentPath).resolve("user-code").resolve(String.valueOf(appId));
        if(!appPath.toFile().exists()){
            //不存在，从 gitee 拉取代码到本地
            try{
                giteeUtil.pullUserAppCode(giteeOwner, giteeRepo, giteeBranch, String.valueOf(appId), appPath.toString());
            }
            catch(Exception e){
                log.warn(e.getMessage());
            }
        }
        //本地存在，直接返回连接即可
        String vscodeUrl = String.format(vscodeUrlTemplate, codeHost, codePort, appId);
        log.info("{} 的vscode预览链接 {}", appId, vscodeUrl);

        return vscodeUrl;
    }

    @Override
    public Boolean confirmVscodeEdit(String token, String appId) {
        LoginUserDTO loginUserDTO = tokenService.getLoginUser(token);
        String userId = loginUserDTO.getUserId();
        if(!redisService.hasKey(TokenConstants.LOGIN_TOKEN_KEY + userId)){
            log.warn("用户{}没有编辑权限", userId);
            return null; //没有编辑权限
        }
        String currentPath = System.getProperty("user.dir");
        Path appPath = Path.of(currentPath).resolve("user-code").resolve(appId);

        try{
            //将 user-code/${appId} 中的内容重新部署到预览容器（目录挂载）
            Map<String, String> files = FileUtil.readAllFiles(appPath);
            String appType = GeneratedAppWriter.determineAppType(files);
            int appNum = AppType.getTypeNum(appType);
            AppBuildUtil.handleApp(Long.valueOf(appId), appPath, appNum, PreviewDeployPath.DEPLOY.getPath(), dockerClient, containerName);

            List<FileDTO> fileDTOs = new ArrayList<>();
            for (Map.Entry<String, String> entry : files.entrySet()) {
                // 去除模型可能多写的 ${appId}/ 前缀，仓库内路径只保留一层目录
                String rel = GeneratedAppWriter.stripAppIdPrefix(appId, entry.getKey());
                FileDTO fileDTO = new FileDTO();
                fileDTO.setFilePath(appId + "/" + rel);
                fileDTO.setFileContent(entry.getValue());
                fileDTOs.add(fileDTO);
            }

            String message = String.format("VSCode 手动编辑更新：%s", appId);
            log.info("gitee commit 入参: owner={}, repo={}, branch={}, message={}, files={}",
                    giteeOwner, giteeRepo, giteeBranch, message, fileDTOs.size());
            String resp = giteeUtil.commitFile(giteeOwner, giteeRepo, message, giteeBranch, fileDTOs);
            log.info("gitee commit 返回: {}", resp);
        }
        catch(Exception e){
            log.error("gitee confirm 失败", e);
        }
        return true;
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
                "### VUE3/VUE3_SPRING 前端 index.html 强制约束（CRITICAL）",
                "1. index.html 只能是「极简壳」：<head> 内仅 charset/viewport/title；<body> 内只允许 `<div id=\"app\"></div>` 与 `<script type=\"module\" src=\"/src/main.js\"></script>`。",
                "2. 严禁在 index.html 中写内联 `<script type=\"module\">`、import 任何 .vue/.js、createApp(...)、或直接使用 @click/v-if/{{ }} 等 Vue 指令。",
                "3. 所有页面结构/样式/交互一律在 src/**/*.vue、src/**/*.css 中修改；src/main.js 只负责 createApp(App).mount('#app')。",
                "4. 若原 index.html 违反上述约束，修改时必须顺手把它改回合规形态。",

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
}
