SET SQL_SAFE_UPDATES=0;

-- Migrate properties
UPDATE PROPERTY SET propertyServiceEnum='GUILD_CRYSTAL_COST' WHERE propertyServiceEnum='GUILD_RAZARION_COST';

-- Box
ALTER TABLE ITEM_BOX_POSSIBILITY CHANGE COLUMN `razarion` `crystals` INT(11) NULL DEFAULT NULL;

-- Unlock
ALTER TABLE ITEM_TYPE CHANGE COLUMN `unlockRazarion` `unlockCrystals` INT(11) NULL DEFAULT NULL  ;
ALTER TABLE GUIDANCE_LEVEL_TASK CHANGE COLUMN `unlockRazarion` `unlockCrystals` INT(11) NULL DEFAULT NULL  ;
ALTER TABLE PLANET CHANGE COLUMN `unlockRazarion` `unlockCrystals` INT(11) NULL DEFAULT NULL  ;

-- Inventory
ALTER TABLE INVENTORY_ARTIFACT CHANGE COLUMN `razarionCoast` `crystalCoast` INT(11) NULL DEFAULT NULL  ;
ALTER TABLE INVENTORY_ITEM CHANGE COLUMN `razarionCoast` `crystalCoast` INT(11) NULL DEFAULT NULL  ;
ALTER TABLE INVENTORY_NEW_USER CHANGE COLUMN `razarion` `crystals` INT(11) NULL DEFAULT NULL  ;

-- Backup
ALTER TABLE BACKUP_USER_STATUS CHANGE COLUMN `razarion` `crystals` INT(11) NOT NULL  ;

-- Game history
UPDATE GAME_HISTORY SET type='CRYSTALS_FROM_BOX' WHERE type='RAZARION_FROM_BOX';
UPDATE GAME_HISTORY SET type='CRYSTALS_BOUGHT' WHERE type='RAZARION_BOUGHT';
ALTER TABLE GAME_HISTORY CHANGE COLUMN `razarion` `crystals` INT(11) NULL DEFAULT NULL;
ALTER TABLE GAME_HISTORY CHANGE COLUMN `deltaRazarion` `deltaCrystals` INT(11) NULL DEFAULT NULL;


SET SQL_SAFE_UPDATES=1;
