SET SQL_SAFE_UPDATES=0;

SET @deleteBefore := "2012-01-03";

DELETE FROM gamedb.GAME_HISTORY where timeStamp < @deleteBefore;

SET SQL_SAFE_UPDATES=1;