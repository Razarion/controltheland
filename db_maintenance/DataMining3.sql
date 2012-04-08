-- CHANGE THIS
SET @TIME_BEFORE = '2012-04-06 01:00:00'; 
SET @TIME_AFTER = '2012-04-06 15:30:00';
SET @CLICKS_ADWORDS = 1483;
-- CHANGE THIS

SET SQL_SAFE_UPDATES=0; 

-- CREATE TABLE SEO ( 
--     id INT NOT NULL AUTO_INCREMENT,
--     userAgent VARCHAR(255) NULL,
--     PRIMARY KEY ( id )
--  );
-- *
-- SELECT count(*) FROM TRACKER_BROWSER_DETAILS;
--   
-- SELECT count(*) FROM TRACKER_BROWSER_DETAILS
--   WHERE userAgent NOT IN (SELECT userAgent FROM SEO);
-- 
-- SELECT * FROM TRACKER_BROWSER_DETAILS
--   WHERE userAgent NOT IN (SELECT userAgent FROM SEO)
--   GROUP BY userAgent;
-- 

SELECT levelName, count(*), count(*)/@CLICKS_ADWORDS*100  
  FROM GAME_HISTORY 
    WHERE 
      TYPE = 5
      AND timeStamp > @TIME_BEFORE 
      AND timeStamp < @TIME_AFTER
    GROUP BY 
      levelName 
    ORDER BY 
      levelName;

SET SQL_SAFE_UPDATES=1;