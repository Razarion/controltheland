SET SQL_SAFE_UPDATES=0;

SET @deleteBefore := "2012-04-14";

DELETE FROM gamedb.TRACKER_EVENT_ITEM where timeStamp < @deleteBefore;
DELETE FROM gamedb.TRACKER_SYNC_INFOS where niceTimeStamp < @deleteBefore;

SET SQL_SAFE_UPDATES=1;