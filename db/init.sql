CREATE TABLE IF NOT EXISTS `addresses` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `street` VARCHAR(100) NOT NULL,
  `city` VARCHAR(100) NOT NULL,
  `state` VARCHAR(2) NOT NULL,
  `zip` VARCHAR(5) NOT NULL,
  `country` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `phone_numbers` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `number` VARCHAR(255) NOT NULL,
    `countrycode` VARCHAR(3) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `accounts` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` CHAR(36) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `address_id` int NULL,
  `phone_id` INT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`address_id`) REFERENCES addresses(`id`),
  FOREIGN KEY (`phone_id`) REFERENCES phone_numbers(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



