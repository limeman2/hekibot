SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

CREATE SCHEMA IF NOT EXISTS `hekibot` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `hekibot` ;

-- -----------------------------------------------------
-- Table `hekibot`.`users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `hekibot`.`users` (
  `name` VARCHAR(32) NULL,
  `coins` INT NULL,
  `time` INT NULL,
  UNIQUE INDEX `name_UNIQUE` (`name` ASC))
ENGINE = InnoDB;

CREATE USER 'user' IDENTIFIED BY 'hejhej';

GRANT ALL ON `hekibot`.* TO 'user';

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
