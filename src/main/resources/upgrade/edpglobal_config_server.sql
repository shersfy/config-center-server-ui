-- 
-- Database Name:	edpglobal_config_server
-- MySQL Version:	5.7.17 MySQL Community Server (GPL) for Linux (x86_64)
-- Author: 		pengy
-- Release Date:	2019-03-21
-- SQL_MODE:		STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION
-- CMD: 			mysql -u -p -h --default-character-set=utf8 < ../edpglobal_config_server.sql

-- ----------------------------
-- Table structure for properties
-- ----------------------------
DROP TABLE IF EXISTS `properties`;
CREATE TABLE `properties` (
	`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
	`label` varchar(255) NOT NULL DEFAULT 'master' COMMENT '标签(或Git分支)',
	`profile` varchar(255) NOT NULL DEFAULT 'dev' COMMENT '环境标签',
	`application` varchar(255) NOT NULL COMMENT '应用名称',
	`key` varchar(255) NOT NULL COMMENT '配置项键',
	`value` text DEFAULT NULL COMMENT '配置项值',
	`comment` varchar(255) DEFAULT NULL COMMENT '配置项说明',
	`create_time` timestamp NOT NULL DEFAULT '2019-01-01 00:00:00' COMMENT '创建时间',
  	`update_time` timestamp NOT NULL DEFAULT '2019-01-01 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
	PRIMARY KEY (`id`),
	UNIQUE KEY `uk_key` (`label`,`profile`,`application`,`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;