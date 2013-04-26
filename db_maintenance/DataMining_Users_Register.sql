-- CHANGE THIS
SET @DATE_START = '2013-04-11'; 
-- CHANGE THIS

-- SELECT DATE(u.registerDate), COUNT(*) FROM USER u
-- WHERE u.accountNonLocked = 1
-- AND u.registerDate >= @DATE_START
-- GROUP BY DATE(u.registerDate);

SELECT DATE(u.registerDate) "Date", COUNT(*) "Registered", 
(SELECT COUNT(*) FROM TRACKER_USER_HISTORY h WHERE DATE(u.registerDate) = DATE(h.loggedIn)) "Unique Logged In", 
(SELECT COUNT(DISTINCT(h.user_name)) FROM TRACKER_USER_HISTORY h WHERE DATE(u.registerDate) = DATE(h.loggedIn)) "Logged in " 
FROM USER u
WHERE u.accountNonLocked = 1
AND u.registerDate >= @DATE_START
GROUP BY DATE(u.registerDate);