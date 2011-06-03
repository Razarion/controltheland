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
import java.io.File;
import java.util.Date;
import java.util.List;

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

    List<File> getLogFiles();

    void backup();

    List<BackupSummary> getBackupSummary();

    void restore(final Date date) throws NoSuchItemTypeException;

    boolean isTestMode();

	boolean isNoGameEngine();

	StartupData getStartupData();

    StartupData readStartupData();

    void saveStartupData(StartupData startupData);
}