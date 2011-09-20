SET SQL_SAFE_UPDATES=0;
ALTER TABLE `gamedb`.`BACKUP_BASE` 
  DROP COLUMN `totalSpent` , 
  DROP COLUMN `totalEarned` , 
  DROP COLUMN `lost` , 
  DROP COLUMN `kills` , 
  DROP COLUMN `created` ;
SET SQL_SAFE_UPDATES=1;