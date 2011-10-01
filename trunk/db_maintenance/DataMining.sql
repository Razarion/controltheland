-- CHANGE THIS
SET @TIME_BEFORE = '2011-09-29 09:36:00'; 
SET @TIME_AFTER = '2011-09-29 22:00:00';
SET @detectedUsers = 266;
SET @twoClickUsers = 89;
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
    maxLevelIndex INT NULL,
    userAgent VARCHAR(1000) NULL,
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
UPDATE tmp_data_mining m SET startups = (SELECT COUNT(*) FROM TRACKER_STARTUP s WHERE s.sessionId = m.sessionId);
UPDATE tmp_data_mining m SET maxLevelIndex = (SELECT max(l.orderIndex) 
          FROM GAME_HISTORY h, GUIDANCE_LEVEL l 
          WHERE h.sessionId = m.sessionId 
          AND l.name = h.levelName
          AND h.type = 5
          GROUP BY h.sessionId);

DROP TABLE IF EXISTS tmp_data_mining_startup;
CREATE TABLE tmp_data_mining_startup ( 
    id INT NOT NULL AUTO_INCREMENT,
    sessionId VARCHAR(255) NULL,
    startupId INT NULL,
    taskCount INT NULL,
    timeStamp DATETIME,
    level VARCHAR(255) NULL,   
    levelIndex INT NULL,
    task1Duration INT NULL,
    PRIMARY KEY ( id )
 );
 
INSERT INTO tmp_data_mining_startup (sessionId, startupId, taskCount, timeStamp, level, levelIndex) SELECT m.sessionId, s.id, COUNT(*), s.timeStamp, s.level, l.orderIndex 
    FROM tmp_data_mining m, TRACKER_STARTUP s, TRACKER_STARTUP_TASK t, GUIDANCE_LEVEL l
    WHERE m.sessionId = s.sessionId
    AND t.dbStartup = s.id
    AND l.name = s.level
    GROUP BY s.id;
    
-- Startups
-- SELECT * FROM tmp_data_mining_startup ORDER BY timeStamp ASC;

-- Detected users
SELECT COUNT(*)"All detected users" FROM tmp_data_mining;        

-- Page Click count / User
SELECT pages"Page Clicks", COUNT(*)"Users", COUNT(*)/@detectedUsers*100"% all Users" FROM tmp_data_mining GROUP BY pages;        

-- Two click users / Second Page
SELECT COUNT(*)"Users with two clicks", COUNT(*)/@twoClickUsers*100"%", secondPage"Second Page"FROM tmp_data_mining WHERE pages = 2 GROUP BY secondPage;        

-- Users / Second Page
SELECT COUNT(*)"Users", COUNT(*)/@detectedUsers*100"% all Users", secondPage"Second Page" FROM tmp_data_mining GROUP BY secondPage;        

-- Two click users entered game but never reached game
SELECT COUNT(*)"Two click users entered game but never reached game", COUNT(*)/@twoClickUsers*100"%" FROM tmp_data_mining WHERE pages = 2 AND secondPage = 'com.btxtech.game.wicket.pages.Game' AND startups = 0;        

-- Game attemps
SELECT COUNT(*)"Game Attemps", COUNT(*)/@detectedUsers*100"% all Users" FROM tmp_data_mining WHERE gameAttemps > 0; 

-- Game attemps but never reached the game
SELECT COUNT(*)"Game attemps but never reached the game", COUNT(*)/@detectedUsers*100"% all Users" FROM tmp_data_mining WHERE gameAttemps > 0 AND startups = 0;        
SELECT * FROM tmp_data_mining WHERE gameAttemps > 0 AND startups = 0;        

-- Game attemps and reached the game
SELECT COUNT(*)"Game attemps and reached the game", COUNT(*)/@detectedUsers*100"% all Users" FROM tmp_data_mining WHERE gameAttemps > 0 AND startups > 0; 
SELECT * FROM tmp_data_mining WHERE gameAttemps > 0 AND startups > 0; 

-- All pages
SELECT COUNT(*)"Count", COUNT(*)/@detectedUsers*100"% all Users", a.page"Page" FROM tmp_data_mining m, TRACKER_PAGE_ACCESS a WHERE a.sessionId = m.sessionId GROUP BY a.page ORDER BY COUNT(*) DESC; 

-- Max Levels Promotions all users
SELECT m.*, l.name FROM tmp_data_mining m, GUIDANCE_LEVEL l where m.maxLevelIndex = l.orderIndex ORDER BY maxLevelIndex DESC;

-- Max Levels Promotions
SELECT count(l.orderIndex)"Users", COUNT(l.orderIndex)/@detectedUsers*100"% all Users" , l.name"Max Level" 
  FROM tmp_data_mining m, GUIDANCE_LEVEL l 
    where m.maxLevelIndex = l.orderIndex 
    GROUP BY l.orderIndex 
    ORDER BY l.orderIndex DESC, count(l.orderIndex) ASC;

-- User with at least one level promotions
SELECT count(*)"User with at least one level promotions", COUNT(*)/@detectedUsers*100"% all Users" FROM tmp_data_mining WHERE maxLevelIndex > 0;

-- User which successfully entered the game but stayed on Noob 1 level
SELECT count(*)"User witch successfully entered the game but stayed on Noob 1 level", COUNT(*)/@detectedUsers*100"% all Users"  FROM tmp_data_mining WHERE startups > 0 AND maxLevelIndex IS NULL;
SELECT * FROM tmp_data_mining WHERE startups > 0 AND maxLevelIndex IS NULL;

-- Startup min max avg of first task
SELECT MIN(startupDuration) / 1000, MAX(startupDuration)/1000 , AVG(startupDuration)/1000 
  FROM TRACKER_STARTUP 
  WHERE id IN (SELECT startupId FROM tmp_data_mining_startup);

-- Startup min max avg of first task
SELECT task, MIN(duration) / 1000, MAX(duration)/1000 , AVG(duration)/1000 
  FROM TRACKER_STARTUP_TASK 
  WHERE dbStartup IN (SELECT startupId FROM tmp_data_mining_startup) 
  GROUP BY task 
  ORDER BY task ASC;

SELECT count(*)"MSIE 6", sum(startups)"startup", sum(gameAttemps)"gameAttemps" FROM tmp_data_mining WHERE userAgent like "%MSIE 6%";        
SELECT count(*)"MSIE 7", sum(startups)"startup", sum(gameAttemps)"gameAttemps" FROM tmp_data_mining WHERE userAgent like "%MSIE 7%";        
SELECT count(*)"MSIE 8", sum(startups)"startup", sum(gameAttemps)"gameAttemps" FROM tmp_data_mining WHERE userAgent like "%MSIE 8%";        
SELECT count(*)"MSIE 9", sum(startups)"startup", sum(gameAttemps)"gameAttemps" FROM tmp_data_mining WHERE userAgent like "%MSIE 9%";        
SELECT count(*)"MSIE ALL", sum(startups)"startup", sum(gameAttemps)"gameAttemps" FROM tmp_data_mining 
   WHERE userAgent like "%MSIE 9%" 
   OR userAgent like "%MSIE 8%" 
   OR userAgent like "%MSIE 7%" 
   OR userAgent like "%MSIE 6%";        

SET SQL_SAFE_UPDATES=1;