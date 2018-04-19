CREATE TABLE `restaurant` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `connection_state` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `opening_from` time NOT NULL,
  `opening_to` time NOT NULL,
  `sending_keep_alive_signal` BOOLEAN DEFAULT FALSE,
  `status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `restaurant_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `connection_state` varchar(255) DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  `sending_keep_alive_signal` BOOLEAN DEFAULT FALSE,
  `status` varchar(255) DEFAULT NULL,
  `restaurant_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKswng3a5dy5rp8i6hmrfndbg5j` (`restaurant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `schedule_unavailable` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `applied` BOOLEAN DEFAULT FALSE,
  `end` datetime NOT NULL,
  `reason` varchar(255) NOT NULL,
  `start` datetime NOT NULL,
  `restaurant_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8q1jns4rjrnr7yq8j0mmgybnh` (`restaurant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `restaurant` (`id`, `connection_state`, `name`, `opening_from`, `opening_to`, `sending_keep_alive_signal`, `status`)
VALUES
(1,'OFFLINE','Restaurant 1','10:00:00','23:00:00',false,'AVAILABLE'),
(2,'OFFLINE','Restaurant 2','10:00:00','23:00:00',false,'AVAILABLE'),
(3,'OFFLINE','Restaurant 3','10:00:00','23:00:00',false,'AVAILABLE'),
(4,'OFFLINE','Restaurant 4','10:00:00','23:00:00',false,'AVAILABLE'),
(5,'OFFLINE','Restaurant 5','10:00:00','23:00:00',false,'AVAILABLE'),
(6,'OFFLINE','Restaurant 6','10:00:00','23:00:00',false,'AVAILABLE'),
(7,'OFFLINE','Restaurant 7','10:00:00','23:00:00',false,'AVAILABLE'),
(8,'OFFLINE','Restaurant 8','10:00:00','23:00:00',false,'AVAILABLE'),
(9,'OFFLINE','Restaurant 9','10:00:00','23:00:00',false,'AVAILABLE');