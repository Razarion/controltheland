SET SQL_SAFE_UPDATES=0;
DELETE FROM `gamedb`.`BACKUP_LEVEL_COMPARISON_SYNC_ITEM_TYPE` where BACKUP_LEVEL_COMPARISON_id in (
  SELECT id FROM `gamedb`.`BACKUP_LEVEL_COMPARISON` where userState_id in (SELECT id FROM `gamedb`.`BACKUP_USER_STATUS` where backupEntry_id = 0));
DELETE FROM `gamedb`.`BACKUP_LEVEL_COMPARISON` where userState_id in (SELECT id FROM `gamedb`.`BACKUP_USER_STATUS` where backupEntry_id = 0);
DELETE FROM `gamedb`.`BACKUP_USER_STATUS` where backupEntry_id = 0;
DELETE FROM `gamedb`.`BACKUP_USER_ITEM_TYPE_ACCESS` where id not in (SELECT userItemTypeAccess_id FROM `gamedb`.`BACKUP_USER_STATUS`);
SET SQL_SAFE_UPDATES=1;