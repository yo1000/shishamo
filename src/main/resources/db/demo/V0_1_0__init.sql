DROP TABLE IF EXISTS `families`;
CREATE TABLE `families` (
  `id`              int(10) unsigned  NOT NULL  COMMENT 'ID',
  `name`            varchar(80)       NOT NULL  COMMENT 'Name',
  PRIMARY KEY(`id`),
  UNIQUE KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Families';

DROP TABLE IF EXISTS `animals`;
CREATE TABLE `animals` (
  `id`              int(10) unsigned  NOT NULL  COMMENT 'ID',
  `name`            varchar(80)       NOT NULL  COMMENT 'Name',
  `japanese_name`   varchar(80)       NOT NULL  COMMENT 'Japanese name',
  `scientific_name` varchar(80)       NOT NULL  COMMENT 'Scientific name',
  `families_id`     int(10) unsigned  NOT NULL  COMMENT 'Families ID',
  PRIMARY KEY(`id`),
  UNIQUE KEY (`scientific_name`),
  CONSTRAINT `index_families_id` FOREIGN KEY (`families_id`) REFERENCES `families` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Animals';

INSERT INTO `families` (`id`, `name`) VALUES (100, 'Osmeridae');
INSERT INTO `families` (`id`, `name`) VALUES (200, 'Delphinidae');

INSERT INTO `animals` (`id`, `name`, `japanese_name`, `scientific_name`, `families_id`)
VALUES (101, 'Spirinchus', 'SHISHAMO', 'Spirinchus lanceolatus', 100);
INSERT INTO `animals` (`id`, `name`, `japanese_name`, `scientific_name`, `families_id`)
VALUES (102, 'Capelin', 'KARAFUTO SHISHAMO', 'Mallotus villosus', 100);

INSERT INTO `animals` (`id`, `name`, `japanese_name`, `scientific_name`, `families_id`)
VALUES (201, 'Bottlenose Dolphin', 'HANDO IRUKA', 'Tursiops truncatus', 200);
