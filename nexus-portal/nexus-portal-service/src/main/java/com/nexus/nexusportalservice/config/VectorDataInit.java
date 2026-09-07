package com.nexus.nexusportalservice.config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Slf4j
@Configuration
public class VectorDataInit implements ApplicationRunner {

    @Autowired
    private MilvusVectorStore vectorStore;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<Document> documents = List.of(
                new Document("null")
        );
        vectorStore.add(documents);
        log.info("vectorStore initialized，一共加载 {} 条平台知识库文档", documents.size());
    }
}
