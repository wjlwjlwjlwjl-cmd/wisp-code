package com.nexus.nexuscommoncore.utils;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;

@SuppressWarnings({"null"})
public class StringUtil {
    /**
     * 将 str 和 patternList 所提供的结构进行匹配
     * 
     * @param str 待匹配字符串
     * @param patternList 匹配规则列表
     * @return 匹配结果
     */
    public static boolean matches(String str, List<String> patternList) {
        if (StringUtils.isEmpty(str) || CollectionUtils.isEmpty(patternList)) {
            return false;
        }
        for (String pattern : patternList) {
            if (isMatch(pattern, str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 查看 url 和 pattern 是否匹配
     * 
     * 匹配规则
     * 1. ?，表示任意一个字符
     * 2. *，表示任意一个层级中的任意字符串
     * 3. **，表示任意多个层级中的任意字符串
     * 
     * @param pattern 匹配规则
     * @param url     需要匹配的 URL
     * @return 匹配结果
     */
    public static boolean isMatch(String pattern, String url) {
        if (StringUtils.isEmpty(url) || StringUtils.isEmpty(pattern)) {
            return false;
        }
        AntPathMatcher matcher = new AntPathMatcher();
        return matcher.match(pattern, url);
    }
}
