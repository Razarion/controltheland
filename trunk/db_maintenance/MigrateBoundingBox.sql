SET SQL_SAFE_UPDATES=0;
ALTER TABLE gamedb.ITEM_TYPE CHANGE COLUMN width imageWidth INT(11) NOT NULL;
ALTER TABLE gamedb.ITEM_TYPE CHANGE COLUMN height imageHeight INT(11) NOT NULL;

ALTER TABLE gamedb.ITEM_TYPE ADD COLUMN boundingBoxWidth INT(11) NULL DEFAULT NULL AFTER imageHeight ;
ALTER TABLE gamedb.ITEM_TYPE ADD COLUMN boundingBoxHeight INT(11) NULL DEFAULT NULL AFTER boundingBoxWidth ;
ALTER TABLE gamedb.ITEM_TYPE ADD COLUMN imageCount INT(11) NULL DEFAULT NULL AFTER boundingBoxHeight ;

UPDATE gamedb.ITEM_TYPE SET boundingBoxWidth = imageWidth * 0.8;
UPDATE gamedb.ITEM_TYPE SET boundingBoxHeight = imageHeight * 0.8;

UPDATE gamedb.ITEM_TYPE SET imageCount = 1;
UPDATE gamedb.ITEM_TYPE AS i, gamedb.ITEM_TURNABLE_TYPE t 
 SET i.imageCount = t.imageCount
   WHERE i.dbTurnableType_id = t.id;

ALTER TABLE `gamedb`.`ITEM_TYPE` DROP FOREIGN KEY `FK283BC0E630003C53` ;
ALTER TABLE `gamedb`.`ITEM_TYPE` DROP COLUMN `dbTurnableType_id`, DROP INDEX `FK283BC0E630003C53` ;
DROP TABLE `gamedb`.`ITEM_TURNABLE_TYPE`;

SET SQL_SAFE_UPDATES=1;