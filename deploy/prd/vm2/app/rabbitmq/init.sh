# 该命令以节点1为例
docker exec -it nexus-stack-rabbitmq02 /bin/bash

#ram节点加入集群

rabbitmqctl stop_app
rabbitmqctl reset
rabbitmqctl join_cluster --ram nexus-stack-rabbitmq01@nexus-stack-rabbitmq01
rabbitmqctl start_app