package com.nexus.nexusportalservice.agent.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.dockerjava.api.DockerClient;
import com.nexus.nexuscommondomain.exception.ServiceException;
import com.nexus.nexusportalservice.domain.AppType;
import com.nexus.nexusportalservice.domain.entity.App;
import com.nexus.nexusportalservice.enums.PreviewDeployPath;
import com.nexus.nexusportalservice.mapper.AppMapper;
import com.nexus.nexusportalservice.utils.AppBuildUtil;
import com.nexus.nexusportalservice.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.HashedMap;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;

@Slf4j
public class BuildPreviewAgent implements NodeAction {
    private DockerClient dockerClient;
    private String containerName;
    private String previewHost;
    private AppMapper appMapper;

    public BuildPreviewAgent(AppMapper appMapper, DockerClient dockerClient, String containerName, String previewHost){
        this.appMapper = appMapper;
        this.dockerClient = dockerClient;
        this.containerName = containerName;
        this.previewHost = previewHost;
    }

    /**
     *
     * @param state  传入内容：appGenerated, appType, appPath
     * @return  传出内容：buildPreview，previewUrl，errorType
     * @throws Exception
     */
    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        System.out.println("\n【BuildPreviewAgent Starting...】\n");
        Map<String, Object> ret = new HashedMap<>();

        boolean appGenerated = Boolean.TRUE.equals(state.value("appGenerated", Boolean.class).orElse(null));

        //上一步生成失败，终止调用链，直接返回
        if(!appGenerated){
            throw new ServiceException("应用生成失败");
        }

        String appType = state.value("appType", String.class).orElse(null);
        Path appPath = state.value("appPath", Path.class).orElse(null);
        String appId = state.value("appId", String.class).orElse(null);

        String previewUrl = "http://" + previewHost + ":80" + "/preview/" + appId + "/#";

        log.info("appId: {}, appPath: {}, appType: {}, previewUrl: {}", appId, appPath, appType, previewUrl);

        try{
            handleApp(Long.valueOf(appId), appPath, appType, PreviewDeployPath.PREVIEW.getPath());

            //2. 更新数据库中预览 Url
            appMapper.update(new LambdaUpdateWrapper<App>()
                    .eq(App::getId, appId)
                    .set(App::getPreviewUrl, previewUrl));

            ret.put("buildPreview", true);
            ret.put("previewUrl", previewUrl);
            ret.put("status", "SUCCESS");

            System.out.println("Build Preview Success");
        }
        catch(Exception e){
            ret.put("status", "FAILED");
            ret.put("buildPreview", false);
            ret.put("errorType", determineBuildErrorType(e));
            ret.put("error", buildDetailedBuildErrorMessage(e, appType));

            System.out.println("Build Preview Failed");
            System.out.println(e.getMessage());
        }
        return ret;
    }

    private void handleApp(Long appId, Path appPath, String appType, String previewDeployPath) throws ServiceException {
        Integer appNum = AppType.getTypeNum(appType);
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

    private String determineBuildErrorType(Exception e) {
        String message = e.getMessage();
        if (message == null) {
            return "UNKNOWN_BUILD_ERROR";
        }

        String lowerMsg = message.toLowerCase(Locale.ROOT);
        if (lowerMsg.contains("npm") || lowerMsg.contains("dependency") || lowerMsg.contains("package")) {
            return "DEPENDENCY_ERROR";
        }
        if (lowerMsg.contains("maven") || lowerMsg.contains("pom.xml")) {
            return "MAVEN_BUILD_ERROR";
        }
        if (lowerMsg.contains("compile") || lowerMsg.contains("compilation")) {
            return "COMPILATION_ERROR";
        }
        if (lowerMsg.contains("port") || lowerMsg.contains("address already in use")) {
            return "PORT_CONFLICT_ERROR";
        }
        if (lowerMsg.contains("docker") || lowerMsg.contains("container")) {
            return "DOCKER_ERROR";
        }
        if (lowerMsg.contains("jar") || lowerMsg.contains("java")) {
            return "JAVA_RUNTIME_ERROR";
        }
        if (lowerMsg.contains("vite") || lowerMsg.contains("build failed")) {
            return "FRONTEND_BUILD_ERROR";
        }

        return "BUILD_ERROR";
    }

    private String buildDetailedBuildErrorMessage(Exception e, String appType) {
        StringBuilder sb = new StringBuilder();
        sb.append("应用类型: ").append(appType).append("\n");
        sb.append("错误类型: ").append(e.getClass().getSimpleName()).append("\n");
        sb.append("错误消息: ").append(e.getMessage()).append("\n");

        // 添加堆栈跟踪的前几行
        StackTraceElement[] stackTrace = e.getStackTrace();
        if (stackTrace != null && stackTrace.length > 0) {
            sb.append("堆栈跟踪:\n");
            int lines = Math.min(5, stackTrace.length);
            for (int i = 0; i < lines; i++) {
                sb.append("  at ").append(stackTrace[i].toString()).append("\n");
            }
        }

        // 如果有原因异常，也包含进来
        Throwable cause = e.getCause();
        if (cause != null) {
            sb.append("原因: ").append(cause.getClass().getSimpleName())
                    .append(": ").append(cause.getMessage()).append("\n");
        }

        // 针对不同错误类型添加提示
        String errorType = determineBuildErrorType(e);
        sb.append("\n可能的问题:\n");
        switch (errorType) {
            case "DEPENDENCY_ERROR":
                sb.append("- package.json 中的依赖配置可能有误\n");
                sb.append("- 依赖版本可能不兼容\n");
                sb.append("- npm registry 可能无法访问\n");
                break;
            case "MAVEN_BUILD_ERROR":
                sb.append("- pom.xml 配置可能有误\n");
                sb.append("- Java 版本可能不匹配\n");
                sb.append("- Maven 依赖下载失败\n");
                break;
            case "COMPILATION_ERROR":
                sb.append("- 代码存在语法错误\n");
                sb.append("- 导入的类或方法不存在\n");
                sb.append("- 类型不匹配\n");
                break;
            case "FRONTEND_BUILD_ERROR":
                sb.append("- vite.config.js 配置可能有误\n");
                sb.append("- Vue 组件语法错误\n");
                sb.append("- 资源引用路径错误\n");
                break;
            default:
                sb.append("- 检查日志以获取更多信息\n");
        }

        return sb.toString();
    }
}
