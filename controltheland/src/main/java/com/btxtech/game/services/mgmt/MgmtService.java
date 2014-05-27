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

package com.btxtech.game.services.mgmt;

import com.btxtech.game.jsre.common.perfmon.PerfmonEnum;
import com.btxtech.game.services.user.User;
import org.apache.wicket.Page;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: Aug 2, 2009
 * Time: 1:59:32 PM
 */
public interface MgmtService {
    static String SERVER_DEBUG_CMS = "cms";
    static String SERVER_DEBUG_EMAIL_VERIFICATION_ALREADY = "Email already verified";
    static String SERVER_DEBUG_GET_UNLOCKED_PLANET = "Get unlocked planet exception";

    Date getStartTime();

    DbViewDTO queryDb(String sql);

    void saveQuery(String query);

    List<String> getSavedQueris();

    void removeSavedQuery(String query);

    MemoryUsageHistory getHeapMemoryUsageHistory();

    MemoryUsageHistory getNoHeapMemoryUsageHistory();

    void saveClientPerfmonData(String sessionId, Map<PerfmonEnum, Integer> workTimes, Map<PerfmonEnum, Map<String, Integer>> workChildTimes, int totalTime);

    List<ClientPerfmonDto> getClientPerfmonData();

    ClientPerfmonDto getClientPerfmonData(String sessionId);

    void sendEmail(User user, String subject, String inString);

    void saveServerDebug(String category, HttpServletRequest request, Page cause, Throwable throwable);

    void saveServerDebug(String category, Throwable throwable);

    String getLogFileText(int lines);
}