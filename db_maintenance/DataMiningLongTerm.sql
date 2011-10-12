-- SET SQL_SAFE_UPDATES=0; 
-- SET SQL_SAFE_UPDATES=1;

SELECT COUNT(*), b.cookieId, MIN(timeStamp), MAX(timeStamp), remoteHost
    FROM
        TRACKER_BROWSER_DETAILS b
    WHERE
        remoteHost NOT LIKE '%hispeed%'
    GROUP BY
        cookieId
    HAVING
         COUNT(*) > 1
    ORDER BY
         COUNT(*) DESC, MAX(timeStamp) ASC;