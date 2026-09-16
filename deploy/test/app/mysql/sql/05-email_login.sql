USE wispcode;
CREATE TABLE `email_user` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `user_id` varchar(60) DEFAULT NULL,
  `username` varchar(30) DEFAULT NULL,
  `email` varchar(60) NOT NULL,
  `password` varchar(30) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
