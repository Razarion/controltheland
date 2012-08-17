SELECT count(*), t.tutorialTaskName FROM TRACKER_TUTORIAL t, TRACKER_BROWSER_DETAILS d 
WHERE tutorialTaskName in ("Build a factory", "Construct Oil Track", "Earn $50", "Construct 3 Laser Tanks", "Destroy General Perry") 
AND t.niceTimeStamp > "2012-08-12"
AND d.remoteHost not like "%hispeed.ch"
AND t.sessionId = d.sessionId
GROUP BY t.tutorialTaskName ORDER BY count(*) DESC 
