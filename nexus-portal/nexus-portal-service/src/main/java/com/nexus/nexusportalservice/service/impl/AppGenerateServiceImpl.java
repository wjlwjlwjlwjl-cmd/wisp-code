package com.nexus.nexusportalservice.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.nexus.nexusportalservice.domain.utils.LocalFileUtil;
import net.bytebuddy.asm.Advice;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.nexus.nexuscommondomain.exception.ServiceException;
import com.nexus.nexusportalservice.domain.AppType;
import com.nexus.nexusportalservice.domain.ModelParsedResult;
import com.nexus.nexusportalservice.domain.dto.AppGenerateRetDTO;
import com.nexus.nexusportalservice.domain.entity.App;
import com.nexus.nexusportalservice.mapper.AppMapper;
import com.nexus.nexusportalservice.service.IAppGenerateService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@SuppressWarnings({ "null" })
public class AppGenerateServiceImpl implements IAppGenerateService {
    private final ChatClient chatClient;
    private final GiteeServiceImpl giteeServiceImpl;
    private final LocalFileStorageImpl localFileStorageImpl;
    private final AppMapper appMapper;

    public AppGenerateServiceImpl(ChatClient chatClient, GiteeServiceImpl giteeServiceImpl,
            LocalFileStorageImpl localFileStorageImpl, AppMapper appMapper) {
        this.chatClient = chatClient;
        this.giteeServiceImpl = giteeServiceImpl;
        this.localFileStorageImpl = localFileStorageImpl;
        this.appMapper = appMapper;
    }

    @Override
    public AppGenerateRetDTO appGenerate(Long appId, String appDoc) {
        String systemPrompt = getSystemPrompt(String.valueOf(appId));
        String userPrompt = getUserPrompt(appDoc);
        log.info(userPrompt);

        //1. 获取 LLM 生成的源代码
        String rawContent = chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .content();

        //2. 根据源代码获取应用类型
        ModelParsedResult.ParsedResult parsedResult = ModelParsedResult.parse(rawContent);
        Map<String, String> files = parsedResult.getFiles();
        String appType = determineAppType(files);
        Path appPath = null;
        try{
            appPath = LocalFileUtil.ensureUsercodeDir();
        }
        catch(IOException e){
            System.out.println(e.getStackTrace());
        }
        int appNum = -1;
        try{
            appNum = AppType.getTypeNum(appType);
        }
        catch(ServiceException e){
            System.out.println(e.getStackTrace());
        }

        //3. 存储、处理生成的代码，统一存放到 user-code/${appId} 目录
        try{
            LocalFileUtil.writeFiles(appId, files);
        }
        catch(IOException e){
            System.out.println(e.getStackTrace());
        }

        String previewUrl = "";
        if(appNum == 0){
            previewUrl = handleHtml(appId, appPath);
        }
        else if(appNum == 1){
            previewUrl = handleVue(appId, appPath);
        }
        else if(appNum == 2){
            previewUrl = handleSpring(appId, appPath);
        }

        //4. 更新数据库信息
        appMapper.update(new LambdaUpdateWrapper<App>()
                .eq(App::getId, appId)
                .set(App::getAppType, appNum));
        appMapper.update(new LambdaUpdateWrapper<App>()
                .eq(App::getId, appId)
                .set(App::getPreviewUrl, previewUrl));

        //5. Gitee MCP todo
        giteeServiceImpl.commit(appId, parsedResult.getFiles());

        AppGenerateRetDTO appGenerateRetDTO = new AppGenerateRetDTO();
        appGenerateRetDTO.setAppId(appId);
        appGenerateRetDTO.setAppTypeNum(appNum);
        appGenerateRetDTO.setPreviewUrl(previewUrl);

        return appGenerateRetDTO;
    }

    public static void main(String[] args) throws IOException {
        Path appPath = LocalFileUtil.ensureUsercodeDir();
        AppGenerateServiceImpl appGenerateService = new AppGenerateServiceImpl(null, null, null, null);
        System.out.println(appGenerateService.handleSpring((long)10000016, appPath.resolve("10000016")));
    }

    //Html不需要额外处理，直接拷贝到app自己的目录即可
    private String handleHtml(Long appId, Path appPath){
        return "192.168.160.131" + ":80/" + appId;
    }

    //Vue 前端应用，在源目录 npm install、npm run build 生成 dist 目录
    private String handleVue(Long appId, Path appPath){
        runProcess("npm install", appPath);
        runProcess("npm run build", appPath); //生成 appPath/appId/dist目录
        return "192.168.160.131" + ":80/" + appId + "/dist";
    }

    //前端同 Vue 前端应用；后端使用 maven 打包，在docker 容器中通过 java -jar 运行 jar 包
    private String handleSpring(Long appId, Path appPath){
        //处理前端部分
        Path appFrontendPath = appPath.resolve("frontend");
        runProcess("npm install", appFrontendPath);
        runProcess("npm run build", appFrontendPath); //生成 appPath/appId/dist目录

        //处理后端部分，运行 Java 后端
        Path appBackendPath = appPath.resolve("backend");
        runProcess("mvn clean package -DskipTests", appBackendPath);
        Path appTargetPath = appBackendPath.resolve("target");
        String jarName = appId + ".jar";
        String cmd = "java -jar " + jarName;
        runProcess(cmd, appTargetPath);
        return "192.168.160.131" + ":80/" + appId + "/frontend/dist";
    }

    private void runProcess(String cmd, Path path) {
        System.out.println("【Running cmd】" + cmd);
        try {
            // Windows 使用 cmd.exe，Linux/macOS 使用 bash
            ProcessBuilder processBuilder;

            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                processBuilder = new ProcessBuilder(
                        "cmd.exe", "/c", cmd
                );
            } else {
                processBuilder = new ProcessBuilder(
                        "bash", "-c", cmd
                );
            }

            // 设置命令执行目录
            processBuilder.directory(path.toFile());

            // 合并标准错误和标准输出
            processBuilder.redirectErrorStream(true);

            // 启动进程
            Process process = processBuilder.start();

            // 实时读取输出
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            process.getInputStream(),
                            StandardCharsets.UTF_8
                    )
            )) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            // 等待命令执行完成
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new RuntimeException(
                        "命令执行失败，退出码：" + exitCode + "，命令：" + cmd
                );
            }

        } catch (IOException e) {
            throw new RuntimeException("执行命令失败：" + cmd, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("命令执行被中断：" + cmd, e);
        }
    }

    private String getUserPrompt(String appDoc) {
        return String.join("\n",
                "【⽤⼾需求⽂档】",
                appDoc,
                "【输出要求】请严格按照系统提⽰的格式输出，不要添加多余解释。");
    }

    private static String determineAppType(Map<String, String> files) {
        if (files == null || files.isEmpty()) {
            return null;
        }
        // 规则1: 如果仅⽣成了⼀个⽂件并且⽂件后缀为.html，则应⽤类型为HTML
        if (files.size() == 1) {
            String singleFile = files.keySet().iterator().next();
            if (singleFile.toLowerCase().endsWith(".html")) {
                return AppType.HTML.getType();
            }
        }
        // 规则2: 如果⽣成的⽂件同时包含.java⽂件和.vue⽂件，则应⽤类型为VUE_SPRING
        boolean hasJavaFile = files.keySet().stream()
                .anyMatch(path -> path.toLowerCase().endsWith(".java"));
        boolean hasVueFile = files.keySet().stream()
                .anyMatch(path -> path.toLowerCase().endsWith(".vue"));
        if (hasJavaFile && hasVueFile) {
            return AppType.VUE3_SPRING.getType();
        }
        // 规则3: 如果既不是HTML类型也不是VUE_SPRING类型，并且⽣成的⽂件中包含.vue⽂件，则
        if (hasVueFile) {
            return AppType.VUE3.getType();
        }
        return "error";
    }

    private String getSystemPrompt(String appId) {
        return String.join("\n",
                "你是资深全栈⼯程师和架构师，精通现代 Web 开发。你的⽬标是严格依据⽤⼾需求⽂档⽣成完整、可运⾏、代码整洁且⻚⾯美观的应⽤代码。",
                "### 应⽤类型决策",
                "根据⽤⼾需求⽂档选择最合适的⼀种应⽤类型进⾏⽣成，注意仅可选择以下三种应⽤类型",
                "1. **HTML**: ⽤⼾明确指出或需求简单，仅需展⽰或简单交互。",
                "2. **VUE3**: ⽤⼾明确指出或需求涉及复杂交互、多⻚⾯路由或组件化开发，但⽆需后端服务。",
                "3. **VUE3_SPRING**: ⽤⼾明确指出或需求⽂档中明确需要后端逻辑。",
                "### 通⽤⽣成规范",
                "- **复杂逻辑**: ⽣成的所有应⽤不要包含复杂逻辑（例如：⾝份认证等）。",
                "- **数据存储**: ⽣成的所有应⽤数据存储不依赖任何第三⽅存储机制。",
                "### 类型详细规范",
                "#### 1. 单个 HTML ⻚⾯ (HTML)",
                "- **结构**: 仅输出⼀个 `index.html` ⽂件。",
                "- **技术**: 只能使⽤ HTML、CSS 和原⽣ JavaScript。禁⽌引⼊外部 CSS/JS库（如 Bootstrap, jQuery）。",
                "- **实现**: CSS 必须内联在 `<head><style>` 中；JS 必须内联在`</body>` 前的 `<script>` 中。",
                "#### 2. Vue3 ⼯程 (VUE3)",
                "- **技术栈**: Vue 3 (Composition API, `<script setup>`), Vite, VueRouter 4.x。",
                "- **⽂件结构**: 必须包含标准⼯程结构（`package.json`,`vite.config.js`, `index.html`, `src/main.js`, `src/App.vue` 等）。",
                "- **配置强制要求**:",
                " - `vite.config.js`: 必须配置 `base: './'`，配置 `@` 别名指向`./src`。",
                " - `router`: 必须使⽤ `createWebHashHistory()`。",
                " - `package.json`: 必须包含 `dev` (`vite`) 和 `build` (`vitebuild`) 脚本。",
                "- **质量保证**:", " - 必须能够通过`npm install`安装项⽬所需依赖，并且能够通过`npm run build`正确完成构建⽣成dist⽬录",
                "#### 3. Vue3 + SpringBoot ⼯程 (VUE3_SPRING)",
                "- **⽬录结构**: 前端代码置于 `frontend/` ⽬录下，后端代码置于`backend/` ⽬录下。",
                "- **前端部分 (frontend/)**: ",
                " - 遵循上述 **VUE3** 的所有规范。",
                " - **API 请求关键**: 前端请求后端接⼝时，URL **必须**统⼀添加前缀 `/"
                        + appId + "/api` (例如 `/" + appId + "/api/users`)。这是⽹关转发规则，务必遵守。",
                "- **后端部分 (backend/)**: ",
                " - **技术栈**: Spring Boot 3.x 、JDK 21、 Maven3.9。",
                " - **代码规范**: 务必通过java⾃⾝语法完成代码不要引⼊其它资源",
                " - **⽂件结构**: 必须包含标准⼯程结构（`pom.xml`,`xxxApplication.java(启动类)` 等）。",
                " - **核⼼依赖**: `pom.xml` 必须继承 `spring-boot-starter-parent`，引⼊ `spring-boot-starter-web`。",
                " - **构建配置**: `pom.xml` 必须包含 `spring-boot-maven-plugin` 以⽀持 `java -jar` 运⾏。项目名称为" +
                appId + "，最后生成的 jar 包名称为 xxx.jar，例如 10000015.jar",
                " - **代码实现**: 所有 Controller 的 `@RequestMapping` 必须以 `/api`开头 (例如 `@RequestMapping(\"/api/users\")`)。注意此处不加："
                        + appId,
                " - **数据存储**: **严禁**依赖 MySQL/Redis 等外部服务。仅使⽤内存(`ConcurrentHashMap`) 或本地⽂件模拟数据库。",
                " - **启动类**: 必须包含标准的 SpringBoot 启动类。",
                "- **质量保证**:",
                " - 必须能够通过`mvn clean package -DskipTests`⽣成jar，并且能够通过`java -jar`正确启动jar包",
                "### 输出格式约束 (CRITICAL)",
                "你必须严格按照以下格式输出，解析器依赖此格式：",
                "1. **第⼀⾏**: 特别注意必须在第⼀⾏输出⽣成应⽤的类型（例如：APP_TYPE=HTML、APP_TYPE=VUE3、APP_TYPE=VUE3_SPRING）。",
                "2. **⽂件内容**: 紧接着按以下格式输出每个⽂件：",
                "FILE: <relative_path>",
                "```<language>",
                "<complete_file_content>",
                "```",
                " - `<relative_path>`: ⽂件的相对路径（如 `index.html`,`frontend/src/App.vue`,`backend/src/main/resources/application.properties`）。",
                " - `<complete_file_content>`: **完整**的⽂件内容，**绝对禁⽌**省略、使⽤占位符或 `// ...`。");
    }
}
