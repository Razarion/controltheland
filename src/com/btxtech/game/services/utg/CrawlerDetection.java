/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.services.utg;

/**
 * User: beat
 * Date: 15.01.2010
 * Time: 23:00:15
 */
public class CrawlerDetection {
    private static final String[] CRAWLERS = {
            "Teoma",
            "alexa",
            "froogle",
            "inktomi",
            "looksmart",
            "URL_Spider_SQL",
            "Firefly",
            "NationalDirectory",
            "Ask Jeeves",
            "TECNOSEEK",
            "InfoSeek",
            "WebFindBot",
            "girafabot",
            "crawler",
            "www.galaxy.com",
            "Googlebot",
            "Scooter",
            "Slurp",
            "appie",
            "FAST",
            "WebBug",
            "Spade",
            "ZyBorg",
            "rabaz",
            "msnbot"};

    public static boolean isCrawler(String userAgentString, String remoteHost) {
        if (userAgentString == null) {
            return true;
        }
        if (findCrawlerString(userAgentString)) {
            return true;
        }
        return findCrawlerString(remoteHost);
    }

    private static boolean findCrawlerString(String userAgentString) {
        for (String crawler : CRAWLERS) {
            if (userAgentString.toUpperCase().contains(crawler.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
}
