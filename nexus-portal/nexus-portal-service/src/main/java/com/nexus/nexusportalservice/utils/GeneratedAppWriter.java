package com.nexus.nexusportalservice.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import com.nexus.nexusportalservice.domain.AppType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeneratedAppWriter {
    //确保代码目录创建
    public static Path ensureUsercodeDir() throws IOException {
        String userDir = System.getProperty("user.dir");
        Path base = Path.of(userDir, "user-code").toAbsolutePath();
        if (!Files.exists(base)) {
            Files.createDirectories(base);
        }
        return base;
    }

    /**
     * 将⽂件写⼊ user-code ⽬录。
     * 
     * @param id    应⽤ ID
     * @param files ⽂件列表
     * @return 应⽤⽬录
     */
    public static Path writeFiles(Long id, Map<String, String> files) throws IOException {
        return writeFiles(id.toString(), files, false);
    }

    /**
     * 将⽂件写⼊ user-code/${appId} ⽬录。
     * 
     * @param id         应⽤ ID
     * @param files      ⽂件列表（文件名，文件内容）
     * @param cleanFirst 是否先清理已存在的⽬录
     * @return 应⽤⽬录   user-code/${appId}
     */
    public static Path writeFiles(String id, Map<String, String> files, boolean cleanFirst) throws IOException {
        Path base = ensureUsercodeDir();
        Path appDir = base.resolve(id);
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
    public static void deleteDirectory(Path directory) throws IOException {
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

    public static String determineAppType(Map<String, String> files) {
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

}
