package com.nexus.nexusportalservice.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

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
    public static String handleHtml(Long appId, Path appPath){
        return "192.168.160.131" + ":80/" + appId;
    }

    //Vue 前端应用，在源目录 npm install、npm run build 生成 dist 目录
    public static String handleVue(Long appId, Path appPath){
        AppBuildUtil.runProcess("npm install", appPath);
        AppBuildUtil.runProcess("npm run build", appPath); //生成 appPath/appId/dist目录
        return "192.168.160.131" + ":80/" + appId + "/dist";
    }

    //前端同 Vue 前端应用；后端使用 maven 打包，在docker 容器中通过 java -jar 运行 jar 包
    public static String handleSpring(Long appId, Path appPath){
        //处理前端部分
        Path appFrontendPath = appPath.resolve("frontend");
        AppBuildUtil.runProcess("npm install", appFrontendPath);
        AppBuildUtil.runProcess("npm run build", appFrontendPath); //生成 appPath/appId/dist目录

        //处理后端部分，运行 Java 后端
        Path appBackendPath = appPath.resolve("backend");
        AppBuildUtil.runProcess("mvn clean package -DskipTests", appBackendPath);
        Path appTargetPath = appBackendPath.resolve("target");
        String jarName = appId + ".jar";
        String cmd = "java -jar " + jarName;
        AppBuildUtil.runProcess(cmd, appTargetPath);
        return "192.168.160.131" + ":80/" + appId + "/frontend/dist";
    }

}
