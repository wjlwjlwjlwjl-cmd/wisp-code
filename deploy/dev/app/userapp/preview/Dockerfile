# 基础镜像构建命令 sudo docker build -t wisp-code/preview .
# ----- 基于 Ubuntu 22.04, JDK 21, 相对体积更大, 但更稳定 -----
FROM eclipse-temurin:21-jdk-jammy

# 非交互模式，避免 tzdata 等包在构建时阻塞
ENV DEBIAN_FRONTEND=noninteractive
# 统一工作目录，前端构建产物将放置于 /home/pro
WORKDIR /workspace

# 1) 依赖安装（最佳实践：更新索引 + 无推荐包 + 构建后清理缓存）
RUN apt-get update && \
apt-get install -y --no-install-recommends \
nginx && \
rm -rf /var/lib/apt/lists/*
# 暴露 Web 端口
EXPOSE 80
# 3) 运行 Nginx（前台进程，容器保持存活）
CMD ["nginx", "-g", "daemon off;"]