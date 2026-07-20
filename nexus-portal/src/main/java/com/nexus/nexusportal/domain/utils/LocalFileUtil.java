package com.nexus.nexusportal.domain.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalFileUtil {
    public static Path ensureUsercodeDir() throws IOException {
        String userDir = System.getProperty("user.dir");
        Path base = Paths.get(userDir, "user-code").toAbsolutePath();
        if (!Files.exists(base)) {
            Files.createDirectories(base);
        }
        return base;
    }

    /**
     * 将⽂件写⼊ usercode ⽬录。
     * 
     * @param id    应⽤ ID
     * @param files ⽂件列表zhuyi
     * @return 应⽤⽬录
     */
    public static Path writeFiles(Long id, Map<String, String> files) throws IOException {
        return writeFiles(id.toString(), files, false);
    }

    /**
     * 将⽂件写⼊ usercode ⽬录。
     * 
     * @param id         应⽤ ID
     * @param files      ⽂件列表
     * @param cleanFirst 是否先清理已存在的⽬录
     * @return 应⽤⽬录
     */
    public static Path writeFiles(String id, Map<String, String> files, boolean cleanFirst) throws IOException {
        Path base = ensureUsercodeDir();
        Path appDir = base.resolve(id);
        log.info("Generated app: " + appDir);
        // 如果需要清理且⽬录存在，先删除旧⽂件
        if (cleanFirst && Files.exists(appDir)) {
            log.info("清理旧代码⽬录: {}", appDir);
            deleteDirectory(appDir);
        }
        if (!Files.exists(appDir)) {
            Files.createDirectories(appDir);
        }
        for (Map.Entry<String, String> e : files.entrySet()) {
            String rel = e.getKey();
            Path target = appDir.resolve(rel).normalize();
            if (!target.startsWith(appDir)) {
                // prevent path traversal
                log.warn("跳过不安全的路径: {}", rel);
                continue;
            }
            if (target.getParent() != null &&
                    !Files.exists(target.getParent())) {
                Files.createDirectories(target.getParent());
            }
            Files.writeString(target, e.getValue(), StandardCharsets.UTF_8);
        }
        return appDir;
    }

    /**
     * 递归删除⽬录
     */
    private static void deleteDirectory(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            return;
        }
        try (var stream = Files.walk(directory)) {
            stream.sorted((a, b) -> b.compareTo(a)) // 逆序，先删除⽂件再删除⽬录
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            log.warn("删除⽂件失败: {}", path, e);
                        }
                    });
        }
    }
}
