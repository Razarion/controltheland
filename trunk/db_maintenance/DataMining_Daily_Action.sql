SELECT DATE_FORMAT(s.timeStamp,'%d.%m.%Y'), ROUND(count(*) / 6)  
  FROM TRACKER_CONNECTION_STATISTICS s, TRACKER_BROWSER_DETAILS d
  WHERE d.remoteHost not like "%hispeed.ch"
  AND s.sessionId = d.sessionId
  GROUP BY DATE_FORMAT(s.timeStamp,'%d.%m.%Y')
  ORDER BY s.timeStamp