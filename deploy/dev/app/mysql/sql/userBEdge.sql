USE frameworkjava_admin;
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `nick_name` varchar(64) NOT NULL COMMENT '昵称',
    `phone_number` varchar(64) NOT NULL COMMENT '电话',
    `password` varchar(255) NOT NULL COMMENT '密码',
    `identity` varchar(16) NOT NULL COMMENT '身份',
    `remark` varchar(50) NULL DEFAULT NULL COMMENT '备注',
    `status` varchar(10) NOT NULL COMMENT '状态',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_phone`(`phone_number`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10000001 CHARACTER SET = utf8mb4 COMMENT = '管理端人员表';
INSERT INTO sys_user(nick_name,phone_number,password,`identity`,remark,status) VALUES
('admin','e64c5f44dc95e4ca77d99136ea2c88c6','15e2b0d3c33891ebb0f1ef609ec419420c20e320ce94c65fbc8c3312448eb225','super_admin',NULL,'enable');