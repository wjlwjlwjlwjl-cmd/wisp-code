# redis 主从容器创建好后 进入一个容器执行的命令，注意节点ip地址

vm1ip=127.0.0.1
vm2ip=127.0.0.2

redis-cli --cluster create ${vm1ip}:6380 ${vm1ip}:6381 ${vm1ip}:6382 ${vm2ip}:6383 ${vm2ip}:6384 ${vm2ip}:6385 --cluster-replicas 1 -a @123 --cluster-yes
