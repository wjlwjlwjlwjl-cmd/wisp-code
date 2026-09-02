package com.nexus.nexusportalservice.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.netty.NettyDockerCmdExecFactory;
import com.nexus.nexuscommondomain.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Configuration
public class DockerClientConfig {
    @Value("${docker.host}")
    private String dockerHost;

    @Value("${docker.cert}")
    private String dockerCertPath;

    @Bean
    public DockerClient dockerClient() throws ServiceException, IOException {
        log.info("Docker host: {}", dockerHost);
        dockerCertPath = "./" + dockerCertPath;
        System.out.printf("Docker cert-path: {%s}\n\n\n\n\n\n", dockerCertPath);
        String path = System.getProperty("user.dir");
        System.out.println(path);

        Path certPath = Path.of(dockerCertPath);
        List<String> files = Files.list(certPath).map(Path::toString).toList();
        for(String file: files){
            System.out.println(file);
        }

        DefaultDockerClientConfig clientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(dockerHost)
                .withDockerTlsVerify(true)
                .withDockerCertPath(dockerCertPath)
                .build();
        return DockerClientBuilder
                .getInstance(clientConfig)
                .withDockerCmdExecFactory(new NettyDockerCmdExecFactory())
                .build();
    }
}
