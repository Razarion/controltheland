SELECT c.created, h.user_name, COUNT(*) FROM TRACKER_USER_HISTORY h, TRACKER_USER_HISTORY c 
WHERE h.loggedIn IS NOT NULL
AND c.created > '2013-04-11'
AND c.user_name = h.user_name
GROUP BY h.user_name
ORDER BY COUNT(*) ASC, c.created ASC