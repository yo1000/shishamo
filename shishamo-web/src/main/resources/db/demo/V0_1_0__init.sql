DROP TABLE IF EXISTS `references`;
CREATE TABLE `references` (
  `id`              int(10) unsigned  NOT NULL  COMMENT 'ID',
  `url`             varchar(800)      NOT NULL  COMMENT 'URL',
  PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='References';

DROP TABLE IF EXISTS `families`;
CREATE TABLE `families` (
  `id`              int(10) unsigned  NOT NULL  COMMENT 'ID',
  `name`            varchar(80)       NOT NULL  COMMENT 'Name',
  `references_id`   int(10) unsigned  NOT NULL  COMMENT 'References ID',
  PRIMARY KEY(`id`),
  UNIQUE KEY `uq_families_name` (`name`),
  CONSTRAINT `fk_families_references_id`  FOREIGN KEY (`references_id`) REFERENCES `references` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Families';

DROP TABLE IF EXISTS `animals`;
CREATE TABLE `animals` (
  `id`              int(10) unsigned  NOT NULL  COMMENT 'ID',
  `name`            varchar(80)       NOT NULL  COMMENT 'Name',
  `japanese_name`   varchar(80)       NOT NULL  COMMENT 'Japanese name',
  `scientific_name` varchar(80)       NOT NULL  COMMENT 'Scientific name',
  `families_id`     int(10) unsigned  NOT NULL  COMMENT 'Families ID',
  `references_id`   int(10) unsigned  NOT NULL  COMMENT 'References ID',
  PRIMARY KEY(`id`),
  UNIQUE KEY  `uq_animals_scientific_name` (`scientific_name`),
  CONSTRAINT  `fk_animals_families_id`    FOREIGN KEY (`families_id`)   REFERENCES `families`   (`id`),
  CONSTRAINT  `fk_animals_references_id`  FOREIGN KEY (`references_id`) REFERENCES `references` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Animals';

DROP TABLE IF EXISTS `impressions`;
CREATE TABLE `impressions` (
  `id`          int(10) unsigned  NOT NULL  COMMENT 'ID',
  `impression`  varchar(80)       NOT NULL  COMMENT 'Impression',
  PRIMARY KEY(`id`),
  CONSTRAINT  `fk_impressions_animals_id` FOREIGN KEY (`id`)  REFERENCES `animals`  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Impressions';

INSERT INTO `references` (`id`, `url`) VALUES (1100, 'https://en.wikipedia.org/wiki/Smelt_(fish)');
INSERT INTO `references` (`id`, `url`) VALUES (1200, 'https://en.wikipedia.org/wiki/Oceanic_dolphin');
INSERT INTO `references` (`id`, `url`) VALUES (1101, 'https://en.wikipedia.org/wiki/Shishamo');
INSERT INTO `references` (`id`, `url`) VALUES (1102, 'https://en.wikipedia.org/wiki/Capelin');
INSERT INTO `references` (`id`, `url`) VALUES (1201, 'https://en.wikipedia.org/wiki/Common_bottlenose_dolphin');

INSERT INTO `families` (`id`, `name`, `references_id`) VALUES (100, 'Osmeridae', 1100);
INSERT INTO `families` (`id`, `name`, `references_id`) VALUES (200, 'Delphinidae', 1200);

INSERT INTO `animals` (`id`, `name`, `japanese_name`, `scientific_name`, `families_id`, `references_id`)
VALUES (101, 'Spirinchus', 'SHISHAMO', 'Spirinchus lanceolatus', 100, 1101);
INSERT INTO `animals` (`id`, `name`, `japanese_name`, `scientific_name`, `families_id`, `references_id`)
VALUES (102, 'Capelin', 'KARAFUTO SHISHAMO', 'Mallotus villosus', 100, 1102);
INSERT INTO `animals` (`id`, `name`, `japanese_name`, `scientific_name`, `families_id`, `references_id`)
VALUES (201, 'Bottlenose Dolphin', 'HANDO IRUKA', 'Tursiops truncatus', 200, 1201);
