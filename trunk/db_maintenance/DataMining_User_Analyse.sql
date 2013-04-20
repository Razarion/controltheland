SELECT u.registerDate, u.lastLoginDate, u.id, h.user_name, COUNT(*) FROM USER u, TRACKER_USER_HISTORY h 
WHERE h.loggedIn IS NOT NULL
AND u.registerDate > '2013-04-11'
AND u.accountNonLocked = true
AND u.name = h.user_name
GROUP BY h.user_name
ORDER BY COUNT(*) ASC, u.registerDate ASC

-- SELECT c.created, h.user_name, COUNT(*) FROM TRACKER_USER_HISTORY h, TRACKER_USER_HISTORY c 
-- WHERE h.loggedIn IS NOT NULL
-- AND c.created > '2013-04-11'
-- AND c.user_name = h.user_name
-- GROUP BY h.user_name
-- ORDER BY COUNT(*) ASC, c.created ASC
-- 