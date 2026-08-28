1. 将 deploy 发送到云服务器（例如 scp）

2. 运行 deploy/app/config/cert 下的 cert.sh（root权限，并且修改 SERVER 地址）

    生成如下文件
    ca-key.pem： CA密钥
    ca.pem：CA证书
    cert.pem： 客户端证书
    extfile.cnf： 客户端证书扩展配置文件
    key.pem： 客户端密钥
    server-cert.pem： 服务端证书
    server-key.pem： 服务端密钥

    其中，ca.pem、server-cert.pem、server-key.pem 放到 /etc/docker

    将 /lib/systemd/system/docker.service中的相应部分替换
    ExecStart=/usr/bin/dockerd -H tcp://0.0.0.0:2376 --tlsverify=true --tlscacert=/etc/docker/ca.pem --tlscert=/etc/docker/server-cert.pem --tlskey=/etc/docker/server-key.pem -H fd:// --containerd=/run/containerd/containerd.sock
    随后重启 docker 服务
