USE wispcode;
CREATE TABLE IF NOT EXISTS email_user(
                `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
                `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户 id',
                `email` VARCHAR(60) NOT NULL COMMENT '用户邮箱',
                `password` VARCHAR(30) NOT NULL COMMENT '用户密码',
                PRIMARY KEY (`id`),
                KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户邮箱账号表';