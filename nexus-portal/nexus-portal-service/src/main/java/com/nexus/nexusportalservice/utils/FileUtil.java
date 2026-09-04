package com.nexus.nexusportalservice.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileUtil {
    /**
     * 将 source 拷贝到 target
     *
     * @param source
     * @param target
     * @throws IOException
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

}
