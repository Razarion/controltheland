-- CHANGE THIS
SET @TIME_BEFORE = '2012-02-28 02:00:00'; 
SET @TIME_AFTER = '2012-02-28 12:00:00';
SET @CLICKS_ADWORDS = 922;
-- CHANGE THIS

SET SQL_SAFE_UPDATES=0; 

-- Case windonws problem

DROP TABLE IF EXISTS tmp_data_mining;
CREATE TABLE tmp_data_mining ( 
    id INT NOT NULL AUTO_INCREMENT,
    sessionId VARCHAR(255) NULL,
    pages INT NULL,
    secondPage VARCHAR(255) NULL,
    gameAttemps INT NULL,
    startups INT NULL,
    startupFails INT NULL,
    maxLevelName VARCHAR(255) NULL,
    userAgent VARCHAR(1000) NULL,
    scrollCount INT NULL,
    syncInfoCount INT NULL,
    selectionCount INT NULL,
    PRIMARY KEY ( id )
 );
 
INSERT INTO tmp_data_mining (sessionId,pages,userAgent) SELECT 
            a.sessionId, COUNT(*), b.userAgent
    FROM
        TRACKER_BROWSER_DETAILS b,
        TRACKER_PAGE_ACCESS a
    WHERE
        b.javaScriptDetected = TRUE 
        AND b.timeStamp > @TIME_BEFORE 
        AND b.timeStamp < @TIME_AFTER
        AND a.sessionId = b.sessionId
        -- Cookie: Beat Chrome, Beat FF
        AND b.cookieId NOT IN ("17B3CDCC-DF2B-4D42-BE18-E4C2917533F4", "248FE9A0-7242-4708-B38B-CD2A20E2D9D2")
    GROUP BY
        b.sessionId
    ORDER BY
        COUNT(*);
        
UPDATE tmp_data_mining m SET secondPage = (SELECT page FROM TRACKER_PAGE_ACCESS a WHERE a.sessionId = m.sessionId LIMIT 1,1);
UPDATE tmp_data_mining m SET gameAttemps = (SELECT COUNT(*) FROM TRACKER_PAGE_ACCESS a WHERE a.sessionId = m.sessionId AND a.page = 'com.btxtech.game.wicket.pages.Game');
UPDATE tmp_data_mining m SET startups = (SELECT count(Distinct startUuid) FROM TRACKER_STARTUP_TASK where sessionId = m.sessionId);
UPDATE tmp_data_mining m SET startupFails = (SELECT COUNT(*) FROM TRACKER_STARTUP_TASK t WHERE t.sessionId = m.sessionId AND failureText IS NOT NULL);
UPDATE tmp_data_mining m SET maxLevelName = (SELECT levelName FROM GAME_HISTORY where id = (
    SELECT MAX(h.id) FROM GAME_HISTORY h, GUIDANCE_LEVEL l 
          WHERE h.sessionId = m.sessionId 
          AND h.type = 5));
UPDATE tmp_data_mining m  SET scrollCount = (SELECT COUNT(*) FROM TRACKER_SCROLLING s, TRACKER_STARTUP_TASK t WHERE m.sessionId = t.sessionId AND t.startUuid = s.startUuid);
UPDATE tmp_data_mining m  SET syncInfoCount = (SELECT COUNT(*) FROM TRACKER_SYNC_INFOS s, TRACKER_STARTUP_TASK t WHERE m.sessionId = t.sessionId AND t.startUuid = s.startUuid);
UPDATE tmp_data_mining m  SET selectionCount = (SELECT COUNT(*) FROM TRACKER_SELECTIONS s, TRACKER_STARTUP_TASK t WHERE m.sessionId = t.sessionId AND t.startUuid = s.startUuid);


DROP TABLE IF EXISTS tmp_data_mining_startup;
-- CREATE TABLE tmp_data_mining_startup ( 
--     id INT NOT NULL AUTO_INCREMENT,
--     sessionId VARCHAR(255) NULL,
--     startupId INT NULL,
--     taskCount INT NULL,
--     timeStamp DATETIME,
--     level VARCHAR(255) NULL,   
--     levelIndex INT NULL,
--     task1Duration INT NULL,
--     PRIMARY KEY ( id )
--  );
 
-- INSERT INTO tmp_data_mining_startup (sessionId, startupId, taskCount, timeStamp, level, levelIndex) SELECT m.sessionId, s.id, COUNT(*), s.timeStamp, s.level, l.orderIndex 
--     FROM tmp_data_mining m, TRACKER_STARTUP_TASK t, GUIDANCE_LEVEL l
--     WHERE m.sessionId = t.sessionId
--     AND l.name = s.level;

-- All
SELECT * FROM tmp_data_mining;

-- Detected users
SELECT @detectedUsers := COUNT(*)"All detected users" FROM tmp_data_mining;        
SELECT @detectedUsers"All detected users", @CLICKS_ADWORDS"Click from AdWords", @detectedUsers/@CLICKS_ADWORDS*100"%";        

-- Page Click count / User
SELECT pages"Page Clicks", COUNT(*)"Users", COUNT(*)/@detectedUsers*100"% all Users" FROM tmp_data_mining GROUP BY pages;        

-- Two click users
SELECT @twoClickUsers := COUNT(*)"Users with two clicks" FROM tmp_data_mining WHERE pages = 2;        

-- Two click users / Second Page
SELECT COUNT(*)"Users with two clicks", COUNT(*)/@twoClickUsers*100"%", secondPage"Second Page"FROM tmp_data_mining WHERE pages = 2 GROUP BY secondPage;        

-- Users / Second Page
SELECT COUNT(*)"Users", COUNT(*)/@detectedUsers*100"% all Users", secondPage"Second Page" FROM tmp_data_mining GROUP BY secondPage;        

-- All pages
SELECT COUNT(*)"Count", COUNT(*)/@detectedUsers*100"% all Users", a.page"Page" FROM tmp_data_mining m, TRACKER_PAGE_ACCESS a WHERE a.sessionId = m.sessionId GROUP BY a.page ORDER BY COUNT(*) DESC; 

-- Two click users entered game but never reached game
SELECT COUNT(*)"Two click users entered game but never reached game", COUNT(*)/@twoClickUsers*100"%" FROM tmp_data_mining WHERE pages = 2 AND secondPage = 'com.btxtech.game.wicket.pages.Game' AND startups = 0;        

-- Game attemps
SELECT @gameAttemps := COUNT(*)"Game Attemps", COUNT(*)/@detectedUsers*100"% all Users" FROM tmp_data_mining WHERE gameAttemps > 0; 

-- Game attemps but never reached the game
SELECT COUNT(*)"Game attemps but never reached the game", COUNT(*)/@gameAttemps*100"% Game Attemps", COUNT(*)/@detectedUsers*100"% all Users" FROM tmp_data_mining WHERE gameAttemps > 0 AND startups = 0;        
SELECT * FROM tmp_data_mining WHERE gameAttemps > 0 AND startups = 0;        

-- Game attemps and reached the game
SELECT @gameReached := COUNT(*)"Game attemps and reached the game", COUNT(*)/@gameAttemps*100"% Game Attemps", COUNT(*)/@detectedUsers*100"% all Users" FROM tmp_data_mining WHERE gameAttemps > 0 AND startups > 0; 
SELECT * FROM tmp_data_mining WHERE gameAttemps > 0 AND startups > 0; 

-- Max Levels Promotions all users
-- SELECT m.*, l.name FROM tmp_data_mining m, GUIDANCE_LEVEL l where m.maxLevelIndex = l.orderIndex ORDER BY maxLevelIndex DESC;

-- Max Levels Promotions
-- SELECT count(l.orderIndex)"Users", COUNT(l.orderIndex)/@gameReached*100"% Game reached", COUNT(l.orderIndex)/@detectedUsers*100"% all Users" , l.name"Max Level" 
-- FROM tmp_data_mining m, GUIDANCE_LEVEL l 
--    where m.maxLevelIndex = l.orderIndex 
--    GROUP BY l.orderIndex 
--    ORDER BY l.orderIndex DESC, count(l.orderIndex) ASC;

-- All Promotions
SELECT * FROM tmp_data_mining WHERE maxLevelName IS NOT NULL;

-- Level promotions
SELECT count(*)"Level promotions" FROM tmp_data_mining WHERE maxLevelName IS NOT NULL;

-- Users at least Noob 3 reached
-- SELECT count(*)"Users Noob 3 reached", COUNT(*)/@gameReached*100"% Game reached", COUNT(*)/@detectedUsers*100"% all Users" FROM `gamedb`.`tmp_data_mining` WHERE maxLevelIndex > 1;

-- User with at least one level promotions
-- SELECT count(*)"User with at least one level promotions", COUNT(*)/@gameReached*100"% Game reached", COUNT(*)/@detectedUsers*100"% all Users" FROM tmp_data_mining WHERE maxLevelIndex > 0;

-- User which successfully entered the game but stayed on Noob 1 level
-- SELECT count(*)"User witch successfully entered the game but stayed on Noob 1 level", COUNT(*)/@gameReached*100"% Game reached", COUNT(*)/@detectedUsers*100"% all Users"  FROM tmp_data_mining WHERE startups > 0 AND maxLevelIndex IS NULL;
-- SELECT * FROM tmp_data_mining WHERE startups > 0 AND maxLevelIndex IS NULL;

-- Startup min max avg of first task
-- SELECT MIN(startupDuration) / 1000, MAX(startupDuration)/1000 , AVG(startupDuration)/1000 
--  FROM TRACKER_STARTUP_TASK 
--  WHERE id IN (SELECT startupId FROM tmp_data_mining_startup);

-- Startup min max avg of first task
-- SELECT task, MIN(duration) / 1000, MAX(duration)/1000 , AVG(duration)/1000 
--  FROM TRACKER_STARTUP_TASK 
--  WHERE dbStartup IN (SELECT startupId FROM tmp_data_mining_startup) 
--  GROUP BY task 
--  ORDER BY task ASC;

SELECT count(*)"MSIE 6", sum(startups)"startup", sum(gameAttemps)"gameAttemps" FROM tmp_data_mining WHERE userAgent like "%MSIE 6%";        
SELECT count(*)"MSIE 7", sum(startups)"startup", sum(gameAttemps)"gameAttemps" FROM tmp_data_mining WHERE userAgent like "%MSIE 7%";        
SELECT count(*)"MSIE 8", sum(startups)"startup", sum(gameAttemps)"gameAttemps" FROM tmp_data_mining WHERE userAgent like "%MSIE 8%";        
SELECT count(*)"MSIE 9", sum(startups)"startup", sum(gameAttemps)"gameAttemps" FROM tmp_data_mining WHERE userAgent like "%MSIE 9%";        
SELECT count(*)"MSIE 6,7,8", sum(startups)"startup", sum(gameAttemps)"gameAttemps" FROM tmp_data_mining 
   WHERE userAgent like "%MSIE 8%" 
   OR userAgent like "%MSIE 7%" 
   OR userAgent like "%MSIE 6%";        

-- Show browser info javascript is disabled
SELECT userAgent, html5Support, sessionId FROM TRACKER_BROWSER_DETAILS WHERE javaScriptDetected = FALSE AND timeStamp > @TIME_BEFORE AND timeStamp < @TIME_AFTER;

-- Show browser info javascript is enabled but HTML5 is disabled
SELECT userAgent, html5Support, sessionId FROM TRACKER_BROWSER_DETAILS WHERE javaScriptDetected = TRUE AND html5Support = FALSE AND timeStamp > @TIME_BEFORE AND timeStamp < @TIME_AFTER;

-- In game analysis
-- SELECT m.sessionId, 
--       (Select count(*) FROM TRACKER_SYNC_INFOS i WHERE m.sessionId = i.sessionId)"SyncInfos", 
--       (Select count(*) FROM TRACKER_SCROLLING s WHERE m.sessionId = s.sessionId)"Scrollings",
--       (Select count(*) FROM TRACKER_SELECTIONS e WHERE m.sessionId = e.sessionId)"Selections" FROM tmp_data_mining m WHERE m.gameAttemps > 0 AND m.startups > 0; 

--  Startup failure
SELECT t.failureText, t.levelName, t.task, m.userAgent, t.sessionId FROM TRACKER_STARTUP_TASK t, tmp_data_mining m WHERE failureText IS NOT NULL AND m.sessionId = t.sessionId;

SET SQL_SAFE_UPDATES=1;