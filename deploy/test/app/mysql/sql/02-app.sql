CREATE DATABASE IF NOT EXISTS wispcode default character set utf8mb4 collate utf8mb4_general_ci;
USE wispcode;
CREATE TABLE `app` (
                       `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '应用主键ID',
                       `user_id` bigint NOT NULL COMMENT '所属用户主键ID',
                       `app_name` varchar(100) DEFAULT NULL COMMENT '应用名称',
                       `app_desc` varchar(100) DEFAULT NULL COMMENT '应用描述',
                       `app_doc` text DEFAULT NULL COMMENT '应用需求文档',
                       `preview_url` varchar(100) DEFAULT NULL COMMENT '应用预览url',
                       `app_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '应用类型：0=html，1=vue3，2=vue3_spring',
                       `app_screenshot` varchar(100) DEFAULT NULL COMMENT '应用截图',
                       PRIMARY KEY (`id`),
                       KEY `idx_apps_user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000001 DEFAULT CHARSET=utf8mb4 COMMENT='应用信息表';
FLUSH PRIVILEGES;

DROP TABLE IF EXISTS `chat_history`;
CREATE TABLE `chat_history` (
                        `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                        `app_id` bigint UNSIGNED NOT NULL COMMENT '应用主键ID',
                        `msg_role` INT NOT NULL COMMENT '消息角色：0=用户，1=AI助手',
                        `content` TEXT NOT NULL COMMENT '消息内容',
                        PRIMARY KEY (`id`),
                        KEY `idx_chat_history_app` (`app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天历史记录';
GRANT ALL PRIVILEGES ON wispcode.* TO  'bitedev'@'%';