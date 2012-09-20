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

import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.perfmon.PerfmonEnum;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: Aug 2, 2009
 * Time: 1:59:32 PM
 */
public interface MgmtService {
    Date getStartTime();

    DbViewDTO queryDb(String sql);

    void saveQuery(String query);

    List<String> getSavedQueris();

    void removeSavedQuery(String query);

    void backup();

    List<BackupSummary> getBackupSummary();

    void restore(final Date date) throws NoSuchItemTypeException;

    void deleteBackupEntry(final Date date) throws NoSuchItemTypeException;

    StartupData getStartupData();

    StartupData readStartupData();

    void saveStartupData(StartupData startupData);

    MemoryUsageHistory getHeapMemoryUsageHistory();

    MemoryUsageHistory getNoHeapMemoryUsageHistory();

    void saveClientPerfmonData(String sessionId, Map<PerfmonEnum, Integer> workTimes, int totalTime);

    List<ClientPerfmonDto> getClientPerfmonData();

    ClientPerfmonDto getClientPerfmonData(String sessionId);
}