package com.wjl.service;

import com.wjl.domain.FileDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GiteeServiceTest {
    @Autowired
    GiteeService giteeService;

    @Test
    void commitFile() throws Exception {
        List<FileDTO> files = new ArrayList<>();
        FileDTO file = new FileDTO();
        file.setFilePath("10000002/test.txt");
        file.setFileContent("hello world");
        files.add(file);
        String resp = giteeService.commitFile("wangs-joyful-home", "wispcode-gitee-repo", "commit test", "master", files);
        System.out.println(resp);
    }
}