SELECT levelTaskName, COUNT(*) FROM GAME_HISTORY 
  WHERE type = 'LEVEL_TASK_COMPLETED'
  AND timeStamp >= '2013-04-11'
  AND levelTaskId IN (3,6)
  GROUP BY levelTaskId;

SELECT levelTaskName, COUNT(*) FROM GAME_HISTORY 
  WHERE type = 'LEVEL_TASK_COMPLETED'
  AND timeStamp >= '2013-04-11'
  AND levelTaskId IN (7,4,5,34)
  GROUP BY levelTaskId;

SELECT levelTaskName, COUNT(*) FROM GAME_HISTORY 
  WHERE type = 'LEVEL_TASK_COMPLETED'
  AND timeStamp >= '2013-04-11'
  AND levelTaskId IN (11,9,10,35,36,8)
  GROUP BY levelTaskId;

SELECT levelTaskName, COUNT(*) FROM GAME_HISTORY 
  WHERE type = 'LEVEL_TASK_COMPLETED'
  AND timeStamp >= '2013-04-11'
  AND levelTaskId IN (12,13,16,14,37,73,15,38,74)
  GROUP BY levelTaskId;

SELECT levelTaskName, COUNT(*) FROM GAME_HISTORY 
  WHERE type = 'LEVEL_TASK_COMPLETED'
  AND timeStamp >= '2013-04-11'
  AND levelTaskId IN (42,19,75,77,22,17,21,23,20,76,39,94)
  GROUP BY levelTaskId;