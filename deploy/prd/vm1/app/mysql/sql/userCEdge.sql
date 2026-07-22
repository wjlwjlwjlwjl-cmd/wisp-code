USE frameworkjava_admin;
DROP TABLE IF EXISTS `app_user`;
CREATE TABLE `app_user` (
    `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `nick_name` varchar(64) NULL DEFAULT NULL COMMENT '昵称',
    `phone_number` varchar(64) NULL DEFAULT NULL COMMENT '电话',
    `email` varchar(64) NULL DEFAULT NULL COMMENT '邮箱 email',
    `avatar` varchar(255) NULL DEFAULT NULL COMMENT '头像',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_phone`(`phone_number`) USING BTREE,
    UNIQUE INDEX `uk_email`(`email`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10000001 CHARACTER SET = utf8mb4 COMMENT = '应用端人员表';