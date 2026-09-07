package com.wjl.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wjl.config.GiteeConfig;
import com.wjl.domain.FileDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class GiteeService {
    @Autowired
    private GiteeConfig giteeConfig;

    private OkHttpClient okHttpClient = new OkHttpClient();
    private ObjectMapper objectMapper = new ObjectMapper();

    // https://gitee.com/api/v5/repos/{owner}/{repo}/contents/{path}
    @Tool(description = "批量提交文件")
    public String commitFile (
            @ToolParam(description = "仓库拥有者") String owner,
            @ToolParam(description = "仓库名称") String repo,
            @ToolParam(description = "提交消息") String message,
            @ToolParam(description = "提交分支") String branch,
            @ToolParam(description = "待推送的文件列表")List<FileDTO> files
            )throws Exception{
        log.info("commiting files, owner: {}, repo: {}, message: {}, branch: {}, file's num: {}", owner, repo, message, branch, files.size());
        List<String> rets = new ArrayList<>();
        for(FileDTO fileDTO: files){
            String filePath = fileDTO.getFilePath();
            String fileContent = fileDTO.getFileContent();
            String resp = writeFile(owner, repo, filePath, fileContent, branch, message);
            rets.add(resp);
        }
        return objectMapper.writeValueAsString(rets);
    }

    //将下载下来的文件存放在 /workspace 的 wispcode-data/${appId} 下
    @Tool(description = "从 wispcode-gitee-repo 更新、拉取远端文件到本地")
    public String pullCodeFromRemote(
            @ToolParam(description = "仓库拥有者") String owner,
            @ToolParam(description = "仓库名称") String repo,
            @ToolParam(description = "仓库分支") String branch,
            @ToolParam(description = "应用 id，对应仓库中的目录") String appId,
            @ToolParam(description = "本地目标目录，用于存放远端代码") Path targetPath
    ){
        //先创建本地目录

        return "";
    }

    private String normalizePath(String path) {
        if (path == null) return "";
        return path.startsWith("/") ? path.substring(1) : path;
    }

    public String buildUrl(String owner, String repo, String path){
        return giteeConfig.getApiBaseUrl() + "repos/" + owner + "/" + repo + "/contents/" + path;
    }

    public String buildContentUrl(String owner, String repo, String remotePath, String branch){
        return giteeConfig.getApiBaseUrl() + "repos/" + owner + "/" + repo + "/contents/" + remotePath + "?ref=" + branch + "&access_token=" + giteeConfig.getAccessToken();
    }

    private String writeFile(String owner, String repo, String filePath, String fileContent, String branch, String message) throws IOException{
        String encodedContent = encodeToBase64(fileContent);
        String url = buildUrl(owner, repo, filePath);
        String bodyJson = objectMapper.createObjectNode()
                .put("content", encodedContent)
                .put("branch", branch)
                .put("message", message)
                .put("access_token", giteeConfig.getAccessToken())
                .toString();
        System.out.println("requesting:\n" + url + "\n");
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(bodyJson, MediaType.parse("application/json")))
                .build();
        try(Response response = okHttpClient.newCall(request).execute()){
            return response.body().string();
        }
    }

    private String encodeToBase64(String rawContent){
        if(rawContent.isBlank()){
            return "";
        }
        return Base64.getEncoder().encodeToString(rawContent.getBytes(StandardCharsets.UTF_8));
    }

    private void downloadDirectory(String owner, String repo, String branch,
                                   String remotePath, Path localDir, AtomicInteger fileCounter) throws Exception {
        String url = buildContentUrl(owner, repo, remotePath, branch);
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("拉取目录失败: " + remotePath + ", code=" + response.code());
            }
            String body = response.body().string();
            JsonNode node = objectMapper.readTree(body);
            if (!node.isArray()) {
                throw new IOException("远程目录不存在或不是目录: " + remotePath);
            }
            Files.createDirectories(localDir);
            for (JsonNode child : node) {
                String name = child.path("name").asText();
                String type = child.path("type").asText();
                String childRemotePath = remotePath == null || remotePath.isBlank()
                        ? name : remotePath + "/" + name;
                Path target = localDir.resolve(name);
                if ("dir".equalsIgnoreCase(type)) {
                    downloadDirectory(owner, repo, branch, childRemotePath, target, fileCounter);
                } else if ("file".equalsIgnoreCase(type)) {
                    log.info("--- 下载文件 ---");
                    downloadFile(owner, repo, branch, childRemotePath, target);
                    fileCounter.incrementAndGet();
                }
            }
        }
    }

    private void downloadFile(String owner, String repo, String branch,
                              String remotePath, Path destination) throws Exception {
        String url = buildContentUrl(owner, repo, remotePath, branch);
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("拉取文件失败: " + remotePath + ", code=" + response.code());
            }
            String body = response.body().string();
            JsonNode node = objectMapper.readTree(body);
            String content = node.path("content").asText(null);
            if (content == null || content.isBlank()) {
                throw new IOException("未获取到文件内容: " + remotePath);
            }
            log.info("文件内容 {}",content);
            byte[] data = Base64.getDecoder().decode(content.replace("\n", ""));
            if (destination.getParent() != null) {
                Files.createDirectories(destination.getParent());
            }
            log.info("文件写入地址 {}",destination.toAbsolutePath());
            Files.write(destination, data);
        }
    }
}
