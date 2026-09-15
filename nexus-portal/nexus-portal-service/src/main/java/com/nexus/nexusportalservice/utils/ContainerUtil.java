package com.nexus.nexusportalservice.utils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.*;
import com.nexus.nexusportalservice.domain.dto.CodeContainerDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Random;

@Slf4j
@Component
public class ContainerUtil {
    @Autowired
    private DockerClient dockerClient;

    private static final Random random = new Random();
    private static final int MIN_PORT = 20001;
    private static final int MAX_PORT = 32767;
    private static final int MAX_RETRY = 10;

    /**
     * 获取可用端口 127.0.0.1
     */
    private int getRandomAvailablePort() {
        for (int i = 0; i < 100; i++) {
            int port = random.nextInt(MAX_PORT - MIN_PORT + 1) + MIN_PORT;
            try (ServerSocket serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"))) {
                return port;
            } catch (Exception ignored) {
            }
        }
        log.warn("无法获取可用端口，尝试100次均失败");
        return -1;
    }

    public Boolean containerExists(String containerId){
        try{
            dockerClient.inspectContainerCmd(containerId).exec();
            return true;
        }
        catch(NotFoundException e){
            return false;
        }
    }

    /**
     * 创建 code‑server 容器，返回宿主机端口
     *
     * @param hostDir docker 主机要绑定到容器的目录
     * @param containerDir 容器要被绑定的目录
     * @return docker 主机被绑定的端口
     */
    public CodeContainerDTO createCodeServer(String hostDir, String containerDir) {
        CodeContainerDTO ret = new CodeContainerDTO();

        for (int retry = 0; retry < MAX_RETRY; retry++) {
            int hostLocalPort = getRandomAvailablePort();
            String containerId = null;
            try {
                // 目录绑定，显式读写模式
                Bind bindMount = new Bind(hostDir, new Volume(containerDir), AccessMode.rw);

                Ports.Binding binding = new Ports.Binding("0.0.0.0", String.valueOf(hostLocalPort));
                PortBinding portBinding = new PortBinding(
                        binding,
                        ExposedPort.tcp(8080)
                );

                HostConfig hostConfig = new HostConfig()
                        .withBinds(bindMount)
                        .withPortBindings(portBinding)
                        .withAutoRemove(true)
                        .withRestartPolicy(RestartPolicy.noRestart());

                CreateContainerResponse resp = dockerClient.createContainerCmd("codercom/code-server:4.137.0")
                        .withName("code-server-" + System.currentTimeMillis())
                        .withUser("root")
                        // 把HostConfig传入
                        .withHostConfig(hostConfig)
                        .withExposedPorts(ExposedPort.tcp(8080))
                        .withWorkingDir(containerDir)
                        .withEnv("PWD=" + containerDir)
                        .withCmd(
                                "--auth", "none",
                                "--bind-addr", "0.0.0.0:8080",
                                "--idle-timeout-seconds", "600" //空闲600秒后直接退出，容器销毁
                        )
                        .exec();

                containerId = resp.getId();
                dockerClient.startContainerCmd(containerId).exec();

                // 等待code‑server服务就绪
                ret.setContainId(containerId);
                ret.setHostLocalPort(hostLocalPort);
                return ret;
                // 服务没就绪，清理容器，进入下一次重试
            } catch (Exception e) {
                // 异常：清理残留容器
                if (containerId != null) {
                    try {
                        dockerClient.removeContainerCmd(containerId).withForce(true).exec();
                    } catch (Exception ignore) {
                    }
                }
                continue;
            }
        }
        return null;
    }

    /**
     * 停止并删除容器
     */
    public void stopAndRemoveContainer(String containerId) {
        try {
            dockerClient.stopContainerCmd(containerId).exec();
            dockerClient.removeContainerCmd(containerId).withForce(true).exec();
        } catch (Exception e) {
            log.warn("停止容器失败");
        }
    }
}
