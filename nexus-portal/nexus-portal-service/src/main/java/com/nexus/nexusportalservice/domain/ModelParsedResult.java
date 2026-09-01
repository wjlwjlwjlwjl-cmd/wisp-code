package com.nexus.nexusportalservice.domain;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

public class ModelParsedResult {

    @Data
    @NoArgsConstructor
    public static class ParsedResult {
        private Map<String, String> files = new LinkedHashMap<>();
    }

    public static ParsedResult parse(String output) {
        ParsedResult result = new ParsedResult();
        if (output == null) {
            return result;
        }
        // 移除 <think>...</think> 标签及其内容
        output = output.replaceAll("(?s)<think>.*?</think>", "");
        List<String> lines = Arrays.asList(output.split("\r?\n"));
        // Parse FILE blocks first
        String currentPath = null;
        StringBuilder buf = null;
        boolean inFence = false;
        for (String line : lines) {
            String t = line;
            if (t.startsWith("FILE:")) {
                // flush previous
                if (currentPath != null && buf != null) {
                    result.files.put(currentPath, buf.toString());
                }
                currentPath = t.substring("FILE:".length()).trim();
                buf = new StringBuilder();
                inFence = false;
                continue;
            }
            if (t.startsWith("```")) {
                // toggle fence, but do not include fence lines
                inFence = !inFence;
                continue;
            }
            if (inFence && buf != null) {
                buf.append(line).append("\n");
            }
        }
        // flush last
        if (currentPath != null && buf != null) {
            result.files.put(currentPath, buf.toString());
        }
        return result;
    }
}
