ALTER TABLE `gamedb`.`BOT_ITEM_COUNT` RENAME TO  `gamedb`.`BOT_ITEM_CONFIG`;
ALTER TABLE `gamedb`.`BOT_ITEM_CONFIG` ADD COLUMN `createDirectly` BIT(1) NOT NULL  AFTER `theCount`;
ALTER TABLE `gamedb`.`BOT_ITEM_CONFIG` ADD COLUMN `regionX` INT(11) NULL DEFAULT NULL AFTER `createDirectly`;
ALTER TABLE `gamedb`.`BOT_ITEM_CONFIG` ADD COLUMN `regionY` INT(11) NULL DEFAULT NULL AFTER `regionX`;
ALTER TABLE `gamedb`.`BOT_ITEM_CONFIG` ADD COLUMN `regionWidth` INT(11) NULL DEFAULT NULL AFTER `regionY`;
ALTER TABLE `gamedb`.`BOT_ITEM_CONFIG` ADD COLUMN `regionHeight` INT(11) NULL DEFAULT NULL AFTER `regionWidth`;
SET SQL_SAFE_UPDATES=0;
UPDATE `gamedb`.`BOT_ITEM_CONFIG` SET createDirectly = TRUE WHERE TYPE = 0;
UPDATE `gamedb`.`BOT_ITEM_CONFIG` SET createDirectly = FALSE WHERE TYPE = 1;
UPDATE `gamedb`.`BOT_ITEM_CONFIG` SET createDirectly = FALSE WHERE TYPE = 2;
UPDATE `gamedb`.`BOT_ITEM_CONFIG` c SET c.`regionX` = (SELECT p.coreRectX FROM `gamedb`.`BOT_CONFIG` p WHERE p.id = c.parent_id) WHERE c.createDirectly = TRUE;
UPDATE `gamedb`.`BOT_ITEM_CONFIG` c SET c.`regionX` = (SELECT p.coreRectX FROM `gamedb`.`BOT_CONFIG` p, `gamedb`.`ITEM_TYPE` i 
                                                          WHERE p.id = c.parent_id AND i.id = c.baseItemType_id AND i.dbMovableType_id is null)
                                                       WHERE c.createDirectly = FALSE;

UPDATE `gamedb`.`BOT_ITEM_CONFIG` c SET c.`regionY` = (SELECT p.coreRectY FROM `gamedb`.`BOT_CONFIG` p WHERE p.id = c.parent_id) WHERE c.createDirectly = TRUE;
UPDATE `gamedb`.`BOT_ITEM_CONFIG` c SET c.`regionY` = (SELECT p.coreRectY FROM `gamedb`.`BOT_CONFIG` p, `gamedb`.`ITEM_TYPE` i 
                                                          WHERE p.id = c.parent_id AND i.id = c.baseItemType_id AND i.dbMovableType_id is null)
                                                       WHERE c.createDirectly = FALSE;

UPDATE `gamedb`.`BOT_ITEM_CONFIG` c SET c.`regionWidth` = (SELECT p.coreRectWidth FROM `gamedb`.`BOT_CONFIG` p WHERE p.id = c.parent_id) WHERE c.createDirectly = TRUE;
UPDATE `gamedb`.`BOT_ITEM_CONFIG` c SET c.`regionWidth` = (SELECT p.coreRectWidth FROM `gamedb`.`BOT_CONFIG` p, `gamedb`.`ITEM_TYPE` i 
                                                          WHERE p.id = c.parent_id AND i.id = c.baseItemType_id AND i.dbMovableType_id is null)
                                                       WHERE c.createDirectly = FALSE;

UPDATE `gamedb`.`BOT_ITEM_CONFIG` c SET c.`regionHeight` = (SELECT p.coreRectHeight FROM `gamedb`.`BOT_CONFIG` p WHERE p.id = c.parent_id) WHERE c.createDirectly = TRUE;
UPDATE `gamedb`.`BOT_ITEM_CONFIG` c SET c.`regionHeight` = (SELECT p.coreRectHeight FROM `gamedb`.`BOT_CONFIG` p, `gamedb`.`ITEM_TYPE` i 
                                                          WHERE p.id = c.parent_id AND i.id = c.baseItemType_id AND i.dbMovableType_id is null)
                                                       WHERE c.createDirectly = FALSE;

SET SQL_SAFE_UPDATES=1;
ALTER TABLE `gamedb`.`BOT_ITEM_CONFIG` DROP COLUMN `type`;

ALTER TABLE `gamedb`.`BOT_CONFIG` DROP COLUMN `coreRectX`;
ALTER TABLE `gamedb`.`BOT_CONFIG` DROP COLUMN `coreRectY`;
ALTER TABLE `gamedb`.`BOT_CONFIG` DROP COLUMN `coreRectWidth`;
ALTER TABLE `gamedb`.`BOT_CONFIG` DROP COLUMN `coreRectHeight`;
ALTER TABLE `gamedb`.`BOT_CONFIG` DROP COLUMN `coreSuperiority`;
ALTER TABLE `gamedb`.`BOT_CONFIG` DROP COLUMN `realmSuperiority`;

