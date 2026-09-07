package com.nexus.nexusportalservice.config;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MilvusConfig {

    @Bean
    public MilvusVectorStore milvusVectorStore(MilvusServiceClient milvusServiceClient,
                                               EmbeddingModel embeddingModel) {
        return MilvusVectorStore.builder(milvusServiceClient, embeddingModel)
                .databaseName("wispcode_db")
                .collectionName("RAG")
                .initializeSchema(true)
                .indexType(IndexType.HNSW)
                .metricType(MetricType.COSINE)
                .build();
    }
}