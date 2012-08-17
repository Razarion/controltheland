SELECT * FROM TRACKER_EVENT_START s, TRACKER_BROWSER_DETAILS d 
WHERE s.timeStamp > "2012-08-12"
AND d.remoteHost not like "%hispeed.ch"
AND s.sessionId = d.sessionId