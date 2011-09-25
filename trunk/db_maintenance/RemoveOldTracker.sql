SET SQL_SAFE_UPDATES=0;

SET @deleteBefore := "2011-09-20";

DELETE FROM gamedb.TRACKER_BROWSER_DETAILS where timeStamp < @deleteBefore;
DELETE FROM gamedb.TRACKER_BROWSER_WINDOW where timeStamp < @deleteBefore;
DELETE FROM gamedb.TRACKER_COMMAND where niceTimeStamp < @deleteBefore;
DELETE FROM gamedb.TRACKER_CONNECTION_STATISTICS where timeStamp < @deleteBefore;
DELETE FROM gamedb.TRACKER_EVENT_ITEM where timeStamp < @deleteBefore;
DELETE FROM gamedb.TRACKER_EVENT_START where timeStamp < @deleteBefore;
DELETE FROM gamedb.TRACKER_PAGE_ACCESS where timeStamp < @deleteBefore;
DELETE FROM gamedb.TRACKER_SCROLLING where timeStamp < @deleteBefore;
DELETE FROM gamedb.TRACKER_SELECTIONS where niceTimeStamp < @deleteBefore;
DELETE FROM gamedb.TRACKER_STARTUP_TASK where dbStartup in (SELECT id FROM TRACKER_STARTUP where timeStamp < @deleteBefore);
DELETE FROM gamedb.TRACKER_STARTUP where timeStamp < @deleteBefore;
DELETE FROM gamedb.TRACKER_TUTORIAL where niceTimeStamp < @deleteBefore;
DELETE FROM gamedb.TRACKER_USER_COMMAND where timeStamp < @deleteBefore;
-- sschwierig DELETE FROM gamedb.TRACKER_USER_HISTORY where timeStamp < @deleteBefore;

SET SQL_SAFE_UPDATES=1;