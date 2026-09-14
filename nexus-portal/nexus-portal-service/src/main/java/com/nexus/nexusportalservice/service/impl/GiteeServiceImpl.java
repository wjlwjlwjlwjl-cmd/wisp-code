package com.nexus.nexusportalservice.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nexus.nexusportalservice.service.IGiteeService;
import com.nexus.nexusportalservice.utils.GeneratedAppWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Gitee 代码仓库服务：直接通过 Gitee v5 Contents REST API 完成提交 / 拉取 / 删除，
 * 不再依赖 MCP Server 与大模型复述代码，避免大模型产出的 JSON 被截断 / 转义错误。
 * 仓库归属信息来自 Nacos 配置：
 *   gitee.user-code.owner / gitee.user-code.repo / gitee.user-code.branch
 *   gitee.apiBaseUrl (默认 https://gitee.com/api/v5/) / gitee.accessToken
 */
@Slf4j
@Service
public class GiteeServiceImpl implements IGiteeService {

    @Value("${gitee.user-code.owner}")
    private String userAppCodeOwner;

    @Value("${gitee.user-code.repo}")
    private String userAppCodeRepo;

    @Value("${gitee.user-code.branch}")
    private String userAppCodeBranch;

    @Value("${gitee.apiBaseUrl:https://gitee.com/api/v5/}")
    private String apiBaseUrl;

    @Value("${gitee.accessToken:}")
    private String accessToken;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    /**
     * 提交（新建或覆盖）应用代码到 Gitee 仓库 ${appId}/ 目录。
     *
     * @param appId   应用 id，用作仓库内的目录名
     * @param appPath 本地应用代码根目录（user-code/${appId}）
     * @param appType 应用类型，写入 commit message
     * @param files   文件（相对路径 -> 内容）
     */
    @Override
    public void commit(String appId, Path appPath, String appType, Map<String, String> files) throws Exception {
        requireToken();
        if (appId == null || appId.isBlank() || files == null || files.isEmpty()) {
            return;
        }
        String message = String.format("appId: %s, appType: %s", appId, appType);
        int ok = 0;
        for (Map.Entry<String, String> entry : files.entrySet()) {
            String rel = GeneratedAppWriter.stripAppIdPrefix(appId, entry.getKey());
            Path filePath = appPath.resolve(rel).toAbsolutePath().normalize();
            String fileContent = Files.readString(filePath, StandardCharsets.UTF_8);
            String repoPath = appId + "/" + rel;
            writeFile(userAppCodeOwner, userAppCodeRepo, repoPath, fileContent, userAppCodeBranch, message);
            ok++;
        }
        log.info("gitee commit success: appId={}, files={}", appId, ok);
    }

    /**
     * 从 Gitee 仓库 ${appId}/ 目录拉取代码到本地目录（会先清空本地目录）。
     */
    @Override
    public void pullUserAppCode(Long appId, Path userCodeBaseDir) throws Exception {
        requireToken();
        Path localPath = userCodeBaseDir.toAbsolutePath().normalize();
        Files.createDirectories(localPath);
        cleanDirectory(localPath);
        int[] counter = {0};
        downloadDirectory(userAppCodeOwner, userAppCodeRepo, userAppCodeBranch, String.valueOf(appId), localPath, counter);
        log.info("gitee pull success: appId={}, files={}, dir={}", appId, counter[0], localPath);
    }

    /**
     * 递归删除 Gitee 仓库中 ${appId}/ 目录。
     */
    @Override
    public void delete(String appId) {
        try {
            requireToken();
            if (appId == null || appId.isBlank()) {
                return;
            }
            ArrayList<String> resps = new ArrayList<>();
            deleteFileRecursive(userAppCodeOwner, userAppCodeRepo, userAppCodeBranch, appId,
                    "删除应用代码 appId=" + appId, resps);
            log.info("gitee delete success: appId={}, ops={}", appId, resps.size());
        } catch (Exception e) {
            log.error("调用 Gitee delete 失败: {}", e.getMessage(), e);
            throw new RuntimeException("调用 Gitee delete 失败: " + e.getMessage(), e);
        }
    }

    // ------------------------------------------------------------------
    // 以下为直接对接 Gitee v5 Contents API 的内部实现
    // ------------------------------------------------------------------

    private void requireToken() {
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalStateException("gitee.accessToken 未配置，请在 Nacos 中配置 gitee.accessToken");
        }
    }

    private String baseUrl() {
        return apiBaseUrl.endsWith("/") ? apiBaseUrl : apiBaseUrl + "/";
    }

    private String normalizePath(String path) {
        if (path == null) {
            return "";
        }
        return path.startsWith("/") ? path.substring(1) : path;
    }

    // 逐段百分号编码路径（保留 / 分隔符，中文/空格安全）
    private String encodePath(String path) {
        String[] segs = normalizePath(path).split("/");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < segs.length; i++) {
            if (i > 0) {
                sb.append('/');
            }
            sb.append(URLEncoder.encode(segs[i], StandardCharsets.UTF_8).replace("+", "%20"));
        }
        return sb.toString();
    }

    private String encodeParam(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }

    private String contentsUrl(String owner, String repo, String path) {
        return baseUrl() + "repos/" + encodePath(owner) + "/" + encodePath(repo)
                + "/contents/" + encodePath(path) + "?access_token=" + encodeParam(accessToken);
    }

    private String contentsUrlWithRef(String owner, String repo, String path, String branch) {
        return baseUrl() + "repos/" + encodePath(owner) + "/" + encodePath(repo)
                + "/contents/" + encodePath(path)
                + "?ref=" + encodeParam(branch) + "&access_token=" + encodeParam(accessToken);
    }

    private HttpResponse<String> httpGet(String url) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(60))
                .GET()
                .build();
        return httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    private HttpResponse<String> httpSend(String method, String url, String jsonBody)
            throws IOException, InterruptedException {
        HttpRequest.BodyPublisher publisher = jsonBody == null
                ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8);
        HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(120))
                .header("Content-Type", "application/json")
                .method(method, publisher)
                .build();
        return httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    private String encodeToBase64(String rawContent) {
        if (rawContent == null || rawContent.isBlank()) {
            return "";
        }
        return Base64.getEncoder().encodeToString(rawContent.getBytes(StandardCharsets.UTF_8));
    }

    // 新建或更新单个文件（contents API：不存在 POST 创建，存在则 PUT 更新）
    private void writeFile(String owner, String repo, String filePath, String fileContent,
                           String branch, String message) throws IOException, InterruptedException {
        String url = contentsUrl(owner, repo, filePath);

        String sha = "";
        try {
            HttpResponse<String> getResp = httpGet(url);
            JsonNode node = objectMapper.readTree(getResp.body());
            sha = node.path("sha").asText("");
        } catch (Exception ignore) {
            // 读取失败按“不存在”处理，走创建流程
        }

        String encodedContent = encodeToBase64(fileContent);
        HttpResponse<String> resp;
        if (sha == null || sha.isBlank()) {
            ObjectNode body = objectMapper.createObjectNode();
            body.put("content", encodedContent);
            body.put("branch", branch);
            body.put("message", message);
            resp = httpSend("POST", url, body.toString());
            log.debug("gitee create file: {} status={}", filePath, resp.statusCode());
        } else {
            ObjectNode body = objectMapper.createObjectNode();
            body.put("message", message);
            body.put("sha", sha);
            body.put("content", encodedContent);
            resp = httpSend("PUT", url, body.toString());
            log.debug("gitee update file: {} status={}", filePath, resp.statusCode());
        }

        if (resp.statusCode() / 100 != 2) {
            throw new IOException("Gitee 写入失败: " + filePath + ", status=" + resp.statusCode()
                    + ", body=" + resp.body());
        }
    }

    private void deleteFileRecursive(String owner, String repo, String branch, String dirPath,
                                     String message, ArrayList<String> resps)
            throws IOException, InterruptedException {
        String url = contentsUrlWithRef(owner, repo, dirPath, branch);
        HttpResponse<String> resp = httpGet(url);
        if (resp.statusCode() / 100 != 2) {
            log.warn("gitee delete 列目录失败: {} status={}", dirPath, resp.statusCode());
            return;
        }
        JsonNode node;
        try {
            node = objectMapper.readTree(resp.body());
        } catch (Exception e) {
            log.warn("gitee delete 解析目录失败: {}", dirPath);
            return;
        }
        if (!node.isArray()) {
            log.warn("gitee delete 目标不是目录: {}", dirPath);
            return;
        }
        for (JsonNode child : node) {
            String type = child.path("type").asText();
            String name = child.path("name").asText();
            String sha = child.path("sha").asText();
            if ("file".equalsIgnoreCase(type)) {
                resps.add(deleteFile(owner, repo, branch, dirPath + "/" + name, message, sha));
            } else if ("dir".equalsIgnoreCase(type)) {
                deleteFileRecursive(owner, repo, branch, dirPath + "/" + name, message, resps);
            }
        }
    }

    private String deleteFile(String owner, String repo, String branch, String path,
                              String message, String sha) throws IOException, InterruptedException {
        String url = baseUrl() + "repos/" + encodePath(owner) + "/" + encodePath(repo)
                + "/contents/" + encodePath(path)
                + "?access_token=" + encodeParam(accessToken)
                + "&branch=" + encodeParam(branch)
                + "&message=" + encodeParam(message)
                + "&sha=" + encodeParam(sha);
        HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(60))
                .DELETE()
                .build();
        HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (resp.statusCode() / 100 != 2) {
            log.warn("gitee 删除文件失败: {} status={}", path, resp.statusCode());
            return "";
        }
        return resp.body();
    }

    private void downloadDirectory(String owner, String repo, String branch, String remotePath,
                                   Path localDir, int[] fileCounter) throws IOException, InterruptedException {
        String url = contentsUrlWithRef(owner, repo, remotePath, branch);
        HttpResponse<String> resp = httpGet(url);
        if (resp.statusCode() / 100 != 2) {
            throw new IOException("拉取目录失败: " + remotePath + ", status=" + resp.statusCode());
        }
        JsonNode node;
        try {
            node = objectMapper.readTree(resp.body());
        } catch (Exception e) {
            throw new IOException("远程目录不存在或不是目录: " + remotePath);
        }
        if (!node.isArray()) {
            throw new IOException("远程目录不存在或不是目录: " + remotePath);
        }
        Files.createDirectories(localDir);
        for (JsonNode child : node) {
            String name = child.path("name").asText();
            String type = child.path("type").asText();
            String childRemotePath = (remotePath == null || remotePath.isBlank()) ? name : remotePath + "/" + name;
            Path target = localDir.resolve(name);
            if ("dir".equalsIgnoreCase(type)) {
                downloadDirectory(owner, repo, branch, childRemotePath, target, fileCounter);
            } else if ("file".equalsIgnoreCase(type)) {
                downloadFile(owner, repo, branch, childRemotePath, target);
                fileCounter[0]++;
            }
        }
    }

    private void downloadFile(String owner, String repo, String branch, String remotePath, Path destination)
            throws IOException, InterruptedException {
        String url = contentsUrlWithRef(owner, repo, remotePath, branch);
        HttpResponse<String> resp = httpGet(url);
        if (resp.statusCode() / 100 != 2) {
            throw new IOException("拉取文件失败: " + remotePath + ", status=" + resp.statusCode());
        }
        JsonNode node;
        try {
            node = objectMapper.readTree(resp.body());
        } catch (Exception e) {
            throw new IOException("解析文件响应失败: " + remotePath);
        }
        String content = node.path("content").asText(null);
        if (content == null || content.isBlank()) {
            throw new IOException("未获取到文件内容: " + remotePath);
        }
        byte[] data = Base64.getDecoder().decode(content.replace("\n", "").replace("\r", ""));
        if (destination.getParent() != null) {
            Files.createDirectories(destination.getParent());
        }
        Files.write(destination, data);
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
