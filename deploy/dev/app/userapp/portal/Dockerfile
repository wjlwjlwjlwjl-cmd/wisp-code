# 基础镜像构建命令 sudo docker build -t wisp-code/portal .
# ------------------ 0. portal 服务基础镜像 -----------------
# ----- 基于 Ubuntu 22.04, JDK 21, 相对体积更大, 但更稳定 -----
FROM eclipse-temurin:21-jdk-jammy

ENV TZ=Asia/Shanghai
ENV MAVEN_VERSION=3.9.11
ENV MAVEN_HOME=/opt/maven
ENV PATH=$MAVEN_HOME/bin:$PATH

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# ---------- 1. 配置国内 Ubuntu 源 + 安装系统依赖 ----------
RUN sed -i 's@archive.ubuntu.com@mirrors.tuna.tsinghua.edu.cn@g' /etc/apt/sources.list && \
    sed -i 's@security.ubuntu.com@mirrors.tuna.tsinghua.edu.cn@g' /etc/apt/sources.list && \
    apt-get update && \
    apt-get install -y --no-install-recommends \
    curl \
    wget && \
    rm -rf /var/lib/apt/lists/*

# ---------- 2. 安装 Maven（稳定缓存层） ----------
RUN wget --tries=5 --timeout=30 \
    https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
    -O /tmp/maven.tar.gz && \
    tar -xzf /tmp/maven.tar.gz -C /opt && \
    ln -s /opt/apache-maven-${MAVEN_VERSION} /opt/maven && \
    ln -s /opt/maven/bin/mvn /usr/local/bin/mvn && \
    rm -f /tmp/maven.tar.gz

# ---------- 3. 安装 Node.js（稳定缓存层） ----------
RUN curl -fsSL --retry 5 --retry-delay 5 \
    https://npmmirror.com/mirrors/node/latest-v20.x/SHASUMS256.txt \
    -o /tmp/node-shasums.txt && \
    NODE_VERSION=$(grep "linux-x64.tar.gz" /tmp/node-shasums.txt | head -1 | awk '{print $2}' | sed 's/node-v//;s/-linux-x64.tar.gz//') && \
    wget --tries=5 --timeout=30 \
    https://npmmirror.com/mirrors/node/v${NODE_VERSION}/node-v${NODE_VERSION}-linux-x64.tar.gz \
    -O /tmp/node.tar.gz && \
    tar -xzf /tmp/node.tar.gz -C /usr/local --strip-components=1 && \
    rm -rf /tmp/node*

CMD ["bash"]