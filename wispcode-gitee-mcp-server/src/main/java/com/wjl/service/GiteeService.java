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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
        if(owner.isBlank() || repo.isBlank() || message.isBlank() ||
            branch.isBlank() || files.isEmpty()){
            return "";
        }
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
            @ToolParam(description = "本地目标目录，用于存放远端代码") String targetDir
    ) throws Exception{
        if(owner.isBlank() || repo.isBlank() || appId.isBlank() ||
                branch.isBlank() || targetDir.isBlank()){
            return "";
        }
        //先创建本地目录
        Path localPath = Paths.get(targetDir).toAbsolutePath().normalize();
        Files.createDirectories(localPath);

        //清空旧内容
        cleanDirectory(localPath);

        //从远端下载内容
        AtomicInteger atomicInteger = new AtomicInteger();
        downloadDirectory(owner, repo, branch, appId, localPath, atomicInteger);

        log.info("============= download complete =============");
        return String.format("download {} files to {}", atomicInteger, localPath);
    }

    @Tool(description = "递归删除 wispcode-gitee-repo 中的某个目录")
    public String deleteRecursive(
            @ToolParam(description = "仓库拥有者") String owner,
            @ToolParam(description = "仓库名称") String repo,
            @ToolParam(description = "仓库分支") String branch,
            @ToolParam(description = "要删除的仓库路径") String dirPath,
            @ToolParam(description = "删除说明信息") String message
    )throws Exception{
        if(owner.isBlank() || repo.isBlank() || dirPath.isBlank() ||
                branch.isBlank() || message.isBlank()){
            return "";
        }
        ArrayList<String> resps = new ArrayList<>();
        deleteFileRecursive(owner, repo, branch, dirPath, message, resps);

        return objectMapper.writeValueAsString(resps);
    }

    private String normalizePath(String path) {
        if (path == null) return "";
        return path.startsWith("/") ? path.substring(1) : path;
    }

    private String buildUrl(String owner, String repo, String path){
        return giteeConfig.getApiBaseUrl() + "repos/" + owner + "/" + repo + "/contents/" + normalizePath(path);
    }

    private String buildContentUrl(String owner, String repo, String remotePath, String branch){
        return giteeConfig.getApiBaseUrl() + "repos/" + owner + "/" + repo + "/contents/" + normalizePath(remotePath) + "?ref=" + branch + "&access_token=" + giteeConfig.getAccessToken();
    }

    private void deleteFileRecursive(String owner, String repo, String branch, String dirPath, String message, ArrayList<String> resps) throws Exception{
        //先获取 dirPath 的所有内容
        String url = buildContentUrl(owner, repo, dirPath, branch);
        Request request = new Request.Builder().url(url).build();
        try(Response response = okHttpClient.newCall(request).execute()){
            if(!response.isSuccessful()){
                log.warn("fail to get dir content when trying to delete dir {}", response.code());
                return;
            }
            String str = response.body().string();
            log.info("deleteFileRecursive: {}", str);
            JsonNode node = objectMapper.readTree(str);
            if(!node.isArray()){
                log.warn("delete not an dir");
                return;
            }
            for(JsonNode subNode: node){
                String type = subNode.path("type").asText();
                String name = subNode.path("name").asText();
                String sha = subNode.path("sha").asText();
                String path = subNode.path("path").asText();

                if("file".equalsIgnoreCase(type)){
                    //文件，删除
                    String filePath = dirPath + "/" + name;
                    log.info("deleting file: {}", filePath);
                    String resp = deleteFile(owner, repo, branch, filePath, message, sha);
                    resps.add(resp);
                }
                else if("dir".equalsIgnoreCase(type)){
                    String newDir = dirPath + '/' + name;
                    log.info("diving into dir: {}", dirPath);
                    deleteFileRecursive(owner, repo, branch, newDir, message, resps);
                }
            }
        }
        catch(IOException e){
            System.out.println(e.getStackTrace());
        }
    }

    private String deleteFile(String owner, String repo, String branch, String dirPath, String message, String sha) throws Exception{
        String url = giteeConfig.getApiBaseUrl() + "repos/" + owner + "/" + repo + "/contents/" + dirPath + "?access_token=" + giteeConfig.getAccessToken() + "&message=" + message + "&sha=" + sha;
        log.info("url: {}", url);
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();
        try(Response response = okHttpClient.newCall(request).execute()){
            if(!response.isSuccessful()){
                log.warn("failed to delete {}", dirPath);
                return "";
            }
            return response.body().string();
        }
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
        log.info("requesting:\n{}\n", url);
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

    private void cleanDirectory(Path dir) throws IOException {
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
            return;
        }
        List<Path> paths = Files.walk(dir)
                .sorted(Comparator.reverseOrder())
                .toList();
        for (Path path : paths) {
            if (!path.equals(dir)) {
                Files.deleteIfExists(path);
            }
        }
    }
}
