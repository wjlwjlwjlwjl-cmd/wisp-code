package com.nexus.nexusportalservice.service.impl;

import com.nexus.nexusportalservice.domain.dto.FileDTO;
import com.nexus.nexusportalservice.service.IGiteeService;
import com.nexus.nexusportalservice.utils.GeneratedAppWriter;
import com.nexus.nexusportalservice.utils.GiteeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Gitee 代码仓库服务：仅做「业务入参 -> GiteeUtil 接口」的适配，真正的 Gitee OpenAPI 调用在 {@link GiteeUtil}。
 * 对外契约 {@link IGiteeService} 的传参 / 返回值保持不变；owner/repo/branch 取自 Nacos 配置
 * gitee.user-code.*（与 IGiteeService 调用方解耦，调用方无需感知）。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GiteeServiceImpl implements IGiteeService {

    private final GiteeUtil giteeUtil;

    @Value("${gitee.user-code.owner}")
    private String userAppCodeOwner;

    @Value("${gitee.user-code.repo}")
    private String userAppCodeRepo;

    @Value("${gitee.user-code.branch}")
    private String userAppCodeBranch;

    /**
     * 提交（新建或覆盖）应用代码到 Gitee 仓库 ${appId}/ 目录。
     *
     * @param appId   应用 id，用作仓库内的目录名
     * @param appPath 本地应用代码根目录（user-code/${appId}），当前实现直接使用 files 内容，不依赖该目录
     * @param appType 应用类型，写入 commit message
     * @param files   文件（相对路径 -> 内容）
     */
    @Override
    public void commit(String appId, Path appPath, String appType, Map<String, String> files) throws Exception {
        if (appId == null || appId.isBlank() || files == null || files.isEmpty()) {
            return;
        }
        String message = String.format("appId: %s, appType: %s", appId, appType);
        List<FileDTO> fileDTOs = new ArrayList<>();
        for (Map.Entry<String, String> entry : files.entrySet()) {
            // 去除模型可能多写的 ${appId}/ 前缀，仓库内路径只保留一层目录
            String rel = GeneratedAppWriter.stripAppIdPrefix(appId, entry.getKey());
            FileDTO fileDTO = new FileDTO();
            fileDTO.setFilePath(appId + "/" + rel);
            fileDTO.setFileContent(entry.getValue());
            fileDTOs.add(fileDTO);
        }
        giteeUtil.commitFile(userAppCodeOwner, userAppCodeRepo, message, userAppCodeBranch, fileDTOs);
        log.info("gitee commit success: appId={}, files={}", appId, fileDTOs.size());
    }

    /**
     * 从 Gitee 仓库 ${appId}/ 目录拉取代码到本地目录（GiteeUtil 内部会先清空本地目录）。
     */
    @Override
    public void pullUserAppCode(Long appId, Path userCodeBaseDir) throws Exception {
        String targetDir = userCodeBaseDir.toAbsolutePath().normalize().toString();
        giteeUtil.pullUserAppCode(userAppCodeOwner, userAppCodeRepo, userAppCodeBranch, String.valueOf(appId), targetDir);
        log.info("gitee pull success: appId={}, dir={}", appId, targetDir);
    }

    /**
     * 递归删除 Gitee 仓库中 ${appId}/ 目录。
     */
    @Override
    public void delete(String appId) {
        try {
            String message = "删除应用代码 appId=" + appId;
            giteeUtil.deleteDirectory(userAppCodeOwner, userAppCodeRepo, userAppCodeBranch, appId, message);
            log.info("gitee delete success: appId={}", appId);
        } catch (Exception e) {
            log.error("调用 Gitee delete 失败: {}", e.getMessage(), e);
            throw new RuntimeException("调用 Gitee delete 失败: " + e.getMessage(), e);
        }
    }
}
