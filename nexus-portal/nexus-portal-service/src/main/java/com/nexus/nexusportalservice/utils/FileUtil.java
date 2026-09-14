package com.nexus.nexusportalservice.utils;

import com.nexus.nexuscommondomain.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Slf4j
public final class FileUtil {

    private static final ConcurrentHashMap<Long, Process> RUNNING_JARS = new ConcurrentHashMap<>();

    private FileUtil() {}

    /**
     * 确保 preview 基础目录存在
     */
    public static Path ensureBaseDir(String path) throws IOException {
        String userDir = System.getProperty("user.dir");
        Path base = Paths.get(userDir, path).toAbsolutePath();
        if (!Files.exists(base)) {
            Files.createDirectories(base);
        }
        return base;
    }

    /**
     * 确保 preview 应用的目录存在
     */
    public static Path ensureAppDir(Long appId, String path) throws IOException {
        Path base = ensureBaseDir(path);
        Path appDir = base.resolve(appId.toString());
        if (!Files.exists(appDir)) {
            Files.createDirectories(appDir);
        }
        return appDir;
    }

    /**
     * 复制目录
     */
    public static void copyDirectory(Path source, Path target) throws IOException {
        if (!Files.exists(source)) {
            throw new IOException("源目录不存在: " + source);
        }
        if (!Files.exists(target)) {
            Files.createDirectories(target);
        }
        try (var stream = Files.walk(source)) {
            for (Path s : (Iterable<Path>) stream::iterator) {
                Path relative = source.relativize(s);
                Path dest = target.resolve(relative);
                if (Files.isDirectory(s)) {
                    if (!Files.exists(dest)) {
                        Files.createDirectories(dest);
                    }
                } else {
                    if (dest.getParent() != null && !Files.exists(dest.getParent())) {
                        Files.createDirectories(dest.getParent());
                    }
                    Files.copy(s, dest, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    //将文件传入 ./tmp 目录下
    public static String saveFile(MultipartFile file, String fileName) throws ServiceException {
        if (file == null || file.isEmpty()) {
            throw new ServiceException("文件为空");
        }
        String userDir = System.getProperty("user.dir");
        Path basePath = Paths.get(userDir, "/tmp").toAbsolutePath();
        try {
            if (!Files.exists(basePath)) {
                Files.createDirectories(basePath);
            }
            String suffix = getSuffix(file.getOriginalFilename());
            fileName = fileName + suffix;
            Path filePath = basePath.resolve(fileName);
            InputStream is = file.getInputStream();
            Files.copy(is, filePath, StandardCopyOption.REPLACE_EXISTING);
            return filePath.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    private static String getSuffix(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    public static Map<String, String> readAllFiles(Path rootDir) throws IOException {
        Map<String, String> files = new LinkedHashMap<>();

        try (Stream<Path> stream = Files.walk(rootDir)) {
            stream.filter(Files::isRegularFile)
                    .forEach(path -> {
                        String relativePath = rootDir.relativize(path)
                                .toString()
                                .replace(File.separatorChar, '/');
                        // 跳过构建产物 / 依赖目录与无意义文件，避免读二进制崩溃、避免撑爆提示词
                        if (shouldSkipPath(relativePath)) {
                            return;
                        }
                        byte[] bytes;
                        try {
                            bytes = Files.readAllBytes(path);
                        } catch (IOException e) {
                            log.warn("readAllFiles 跳过无法读取的文件: {}", relativePath);
                            return;
                        }
                        // 二进制文件（含 NUL）跳过
                        if (isBinary(bytes)) {
                            log.debug("readAllFiles 跳过二进制文件: {}", relativePath);
                            return;
                        }
                        // 严格 UTF-8 解码，非 UTF-8 文本跳过而不是抛异常
                        String content;
                        try {
                            content = StandardCharsets.UTF_8.newDecoder()
                                    .onMalformedInput(CodingErrorAction.REPORT)
                                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                                    .decode(ByteBuffer.wrap(bytes));
                        } catch (CharacterCodingException e) {
                            log.warn("readAllFiles 跳过非 UTF-8 文本文件: {}", relativePath);
                            return;
                        }
                        files.put(relativePath, content);
                    });
        }

        return files;
    }

    // 构建产物 / 依赖 / IDE / 锁文件等目录名（任意层级出现即整目录跳过）
    private static final Set<String> SKIP_DIR_NAMES = Set.of(
            "node_modules", "dist", "target", "build", "out",
            ".git", ".idea", ".vscode", ".mvn", ".gradle",
            "coverage", ".next", ".nuxt", ".cache", ".pnpm-store"
    );

    // 需要跳过的具体文件名（文本但无编辑价值 / 系统文件）
    private static final Set<String> SKIP_FILE_NAMES = Set.of(
            "package-lock.json", "yarn.lock", "pnpm-lock.yaml",
            ".DS_Store", "npm-debug.log"
    );

    private static boolean shouldSkipPath(String relativePath) {
        String[] segments = relativePath.split("/");
        for (int i = 0; i < segments.length; i++) {
            String seg = segments[i];
            if (SKIP_DIR_NAMES.contains(seg)) {
                return true;
            }
            // 末段为文件时，额外按文件名过滤
            if (i == segments.length - 1 && SKIP_FILE_NAMES.contains(seg)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isBinary(byte[] bytes) {
        int limit = Math.min(bytes.length, 8000);
        for (int i = 0; i < limit; i++) {
            if (bytes[i] == 0) {
                return true;
            }
        }
        return false;
    }
}