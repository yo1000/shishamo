DROP TABLE IF EXISTS `publisher`;
CREATE TABLE `publisher` (
  `id` int(10) unsigned NOT NULL COMMENT '出版社ID',
  `name` varchar(40) NOT NULL COMMENT '出版社名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='出版社';

DROP TABLE IF EXISTS `book`;
CREATE TABLE `book` (
  `isbn` bigint(19) NOT NULL COMMENT 'ISBN',
  `title` varchar(128) NOT NULL COMMENT 'タイトル',
  `publisher_id` int(10) unsigned NOT NULL COMMENT '出版社ID',
  `author` varchar(40) NOT NULL COMMENT '著者',
  PRIMARY KEY (`isbn`),
  KEY `publisher_id` (`publisher_id`),
  CONSTRAINT `book_ibfk_1` FOREIGN KEY (`publisher_id`) REFERENCES `publisher` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='書籍';
