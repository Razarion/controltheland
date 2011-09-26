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
    PRIMARY KEY ( id )
 );
 
INSERT INTO tmp_data_mining (sessionId,pages) SELECT 
            a.sessionId, COUNT(*)
    FROM
        TRACKER_BROWSER_DETAILS b,
        TRACKER_PAGE_ACCESS a
    WHERE
        b.javaScriptDetected = TRUE 
        AND b.timeStamp > '2011-09-25 17:24:00' 
        AND b.timeStamp < '2011-09-26 08:00:00' 
        AND a.sessionId = b.sessionId
        AND b.cookieId NOT IN ("17B3CDCC-DF2B-4D42-BE18-E4C2917533F4")
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
    PRIMARY KEY ( id )
 );
 
INSERT INTO tmp_data_mining_startup (sessionId, startupId, taskCount, timeStamp, level, levelIndex) SELECT m.sessionId, s.id, COUNT(*), s.timeStamp, s.level, l.orderIndex 
    FROM tmp_data_mining m, TRACKER_STARTUP s, TRACKER_STARTUP_TASK t, GUIDANCE_LEVEL l
    WHERE m.sessionId = s.sessionId
    AND t.dbStartup = s.id
    AND l.name = s.level
    GROUP BY s.id;

-- Startups
SELECT * FROM tmp_data_mining_startup ORDER BY timeStamp ASC;

-- Detected users
SELECT COUNT(*) FROM tmp_data_mining;        

-- Page Click count / User
SELECT pages"Page Clicks", COUNT(*)"Users" FROM tmp_data_mining GROUP BY pages;        

-- Two click users / Second Page
SELECT COUNT(*)"Users with two clicks", secondPage"Second Page" FROM tmp_data_mining WHERE pages = 2 GROUP BY secondPage;        

-- Users / Second Page
SELECT COUNT(*)"Users", secondPage"Second Page" FROM tmp_data_mining GROUP BY secondPage;        

-- Two click users entered game but never reached game
SELECT COUNT(*) FROM tmp_data_mining WHERE pages = 2 AND secondPage = 'com.btxtech.game.wicket.pages.Game' AND startups = 0;        

-- Game attemps but never reached the game
SELECT COUNT(*)"Game Attemps" FROM tmp_data_mining WHERE gameAttemps > 0; 

-- Game attemps but never reached the game
SELECT * FROM tmp_data_mining WHERE gameAttemps > 0 AND startups = 0;        
SELECT COUNT(*)"Game attemps but never reached the game" FROM tmp_data_mining WHERE gameAttemps > 0 AND startups = 0;        

-- Game attemps but never reached the game
SELECT COUNT(*)"Game attemps and reached the game" FROM tmp_data_mining WHERE gameAttemps > 0 AND startups > 0; 

-- All pages
SELECT COUNT(*)"Count", a.page"Page" FROM tmp_data_mining m, TRACKER_PAGE_ACCESS a WHERE a.sessionId = m.sessionId GROUP BY a.page ORDER BY COUNT(*) DESC; 

-- Max Levels Promotions all users
SELECT m.*, l.name FROM tmp_data_mining m, GUIDANCE_LEVEL l where m.maxLevelIndex = l.orderIndex ORDER BY maxLevelIndex DESC;

-- Max Levels Promotions
SELECT count(l.orderIndex)"Users", l.name"Max Level" 
  FROM tmp_data_mining m, GUIDANCE_LEVEL l 
    where m.maxLevelIndex = l.orderIndex 
    GROUP BY l.orderIndex 
    ORDER BY l.orderIndex DESC, count(l.orderIndex) ASC;

-- User with at least one level promotions
SELECT count(*)"User with at least one level promotions" FROM tmp_data_mining WHERE maxLevelIndex > 0;

-- SELECT * FROM tmp_data_mining where pages = 2 and secondPage = 'com.btxtech.game.wicket.pages.Game';        
-- SELECT count(*) FROM tmp_data_mining where secondPage = 'com.btxtech.game.wicket.pages.Game';        
         
SET SQL_SAFE_UPDATES=1;