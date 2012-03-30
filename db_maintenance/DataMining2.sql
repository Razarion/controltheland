-- CHANGE THIS
SET @TIME_BEFORE = '2012-03-07 00:00:00'; 
SET @TIME_AFTER = '2012-03-22 23:59:59';
-- CHANGE THIS

SET SQL_SAFE_UPDATES=0; 

SELECT COUNT(*)"Promotions" FROM GAME_HISTORY 
  WHERE 
    type = 5 
  AND 
    timeStamp > @TIME_BEFORE 
  AND 
    timeStamp < @TIME_AFTER;


SELECT levelName, COUNT(*) FROM GAME_HISTORY 
  WHERE 
    type = 5 
  AND 
    timeStamp > '2012-03-07 00:00:00' 
  GROUP BY
    levelName
  ORDER BY
    levelName;
    

SELECT levelTaskName, COUNT(*) FROM GAME_HISTORY 
  WHERE 
    type = 6 
  AND 
    timeStamp > '2012-03-07 00:00:00' 
  GROUP BY
    levelTaskName
  ORDER BY
    COUNT(*) DESC;
    
SELECT COUNT(SUBSTRING(referer, 1, 20)), referer, MIN(timeStamp), MAX(timeStamp), (MAX(timeStamp)- MIN(timeStamp)) / 86400 FROM TRACKER_BROWSER_DETAILS
  WHERE 
    timeStamp > '2012-03-07 00:00:00' 
  GROUP BY
    SUBSTRING(referer, 1, 20)
  ORDER BY
    COUNT(SUBSTRING(referer, 1, 20)) DESC;


SET SQL_SAFE_UPDATES=1;