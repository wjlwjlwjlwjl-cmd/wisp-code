package com.nexus.nexusportalservice.utils;


import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectExecResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;

@Slf4j
public class AppBuildUtil {
    public static void runProcess(String cmd, Path path) {
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

    //Html不需要额外处理，直接拷贝到app自己的目录即可
    public static void handleHtml(Long appId, Path appPath){}

    //Vue 前端应用，在源目录 npm install、npm run build 生成 dist 目录
    public static void handleVue(Long appId, Path appPath){
        AppBuildUtil.runProcess("npm install", appPath);
        AppBuildUtil.runProcess("npm run build", appPath); //生成 appPath/appId/dist目录
    }

    //前端同 Vue 前端应用；后端使用 maven 打包，在docker 容器中通过 java -jar 运行 jar 包
    public static void handleSpring(Long appId, Path appPath){
        //处理前端部分
        Path appFrontendPath = appPath.resolve("frontend");
        AppBuildUtil.runProcess("npm install", appFrontendPath);
        AppBuildUtil.runProcess("npm run build", appFrontendPath); //生成 appPath/appId/dist目录

        //处理后端部分，在容器中运行 Java 后端
        Path appBackendPath = appPath.resolve("backend");
        AppBuildUtil.runProcess("mvn clean package -DskipTests", appBackendPath);
        Path appTargetPath = appBackendPath.resolve("target");
        String jarName = appId + ".jar";
        String cmd = "java -jar " + jarName;
        AppBuildUtil.runProcess(cmd, appTargetPath);
    }

    ////////////////////////////////////////

    /**
     * 使用 appId 生成固定端口，范围：8001-9999
     * @param appId
     * @param previewDeployPath user-deploy，那么就直接部署在 8080 端口
     * @return 分配的端口
     */
    private static int generatePort(Long appId, String previewDeployPath) {
        // 8001 + (appId % 1999) 可以生成 8001-9999 范围内的端口
        if ("user-deploy".equals(previewDeployPath)) {
            return 8080; //部署固定使用8080端口
        }
        int port = 8001 + (int) (appId % 1999);
        log.info("为 appId {} 分配端口: {}", appId, port);
        return port;
    }

    /**
     *
     * @param containerId
     * @param command
     * @param actionDesc
     * @param dockerClient
     * @throws IOException
     * @throws InterruptedException
     */
    private static void execInContainer(String containerId, String command, String actionDesc, DockerClient dockerClient)
            throws IOException, InterruptedException {
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();

        String execId = dockerClient.execCreateCmd(containerId)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withCmd("bash", "-c", command)
                .exec()
                .getId();

        dockerClient.execStartCmd(execId)
                .exec(new ExecStartResultCallback(stdout, stderr))
                .awaitCompletion();

        InspectExecResponse inspect = dockerClient.inspectExecCmd(execId).exec();
        Long exitCode = inspect.getExitCodeLong();
        if (exitCode == null || exitCode != 0) {
            String errorOutput = stderr.toString(StandardCharsets.UTF_8);
            throw new IOException(actionDesc + "失败，退出码=" + exitCode + "，错误输出: " + errorOutput);
        }

        if (stdout.size() > 0) {
            log.info("{} 成功，输出: {}", actionDesc, stdout.toString(StandardCharsets.UTF_8).trim());
        } else {
            log.info("{} 成功", actionDesc);
        }
    }

    /**
     * 更新 nginx config
     *
     * @param containerId
     * @param appId
     * @param port
     * @param dockerClient
     */
    private static void updateNginxConfig(String containerId, Long appId, int port, DockerClient dockerClient) {
        log.info("更新 nginx 配置，appId={}, port={}", appId, port);
        String scriptPath = "/workspace/scripts/update_nginx_location.sh";
        String updateCommand = scriptPath + " " + appId + " " + port;
        String reloadCommand = "nginx -s reload";

        try {
            execInContainer(containerId, updateCommand, "更新 nginx 配置", dockerClient);
            execInContainer(containerId, reloadCommand, "重载 nginx", dockerClient);
            log.info("容器 {} 的 nginx 配置已更新并重载，appId={}, port={}", containerId, appId, port);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            log.error("更新 nginx 配置时线程被中断，appId={}, port={}", appId, port, ie);
            throw new RuntimeException("更新 nginx 配置过程中线程被中断", ie);
        } catch (Exception e) {
            log.error("更新 nginx 配置失败，appId={}, port={}, 错误: {}", appId, port, e.getMessage(), e);
            throw new RuntimeException("更新 nginx 配置失败: " + e.getMessage(), e);
        }
    }

    private static void executeJarInContainer(Long appId, String jarFileName, DockerClient dockerClient, String previewDeployPath, String containerName) {
        try {
            // 查找容器
            Container container = dockerClient.listContainersCmd()
                    .withShowAll(true)
                    .exec()
                    .stream()
                    .filter(c -> Arrays.asList(c.getNames()).contains("/" + containerName))
                    .findFirst()
                    .orElse(null);

            if (container == null) {
                log.warn("未找到容器: {}", containerName);
                return;
            }

            String containerId = container.getId();

            int port = generatePort(appId, previewDeployPath);

            String jarPathInContainer = "/workspace/user-preview" + "/" + appId + "/" + jarFileName;
            if ("user-deploy".equals(previewDeployPath)) {
                jarPathInContainer = "/workspace/user-deploy" + "/" + jarFileName;
            }


            // 使用 nohup 在后台执行 jar 包，指定端口号
            String command = "nohup java -jar " + jarPathInContainer + " --server.port=" + port + " > /dev/null 2>&1 &";

            log.info("在容器 {} 中执行命令: {}", containerName, command);

            String execId = dockerClient.execCreateCmd(containerId)
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .withCmd("bash", "-c", command)
                    .exec()
                    .getId();

            dockerClient.execStartCmd(execId).start();

            log.info("已在容器 {} 中启动 jar 包: {}，端口: {}", containerName, jarPathInContainer, port);

            log.info("--- previewDeployPath: {}" , previewDeployPath);
            // 动态更新 nginx 配置
            if ("user-preview".equals(previewDeployPath)) {
                updateNginxConfig(containerId, appId, port, dockerClient);
            }

        } catch (Exception e) {
            log.error("在容器中执行 jar 包失败，容器名={}, appId={}, jar={}, 错误: {}",
                    containerName, appId, jarFileName, e.getMessage(), e);
            throw new RuntimeException("在容器中执行 jar 包失败: " + e.getMessage(), e);
        }
    }
}
