package com.btxtech.game.services.mgmt;

import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;

import java.util.Date;
import java.util.List;

/**
 * User: beat
 * Date: 08.08.13
 * Time: 01:24
 */
public interface BackupService {
    void backup();

    List<BackupSummary> getBackupSummary();

    void restore(final Date date) throws NoSuchItemTypeException;

    void deleteBackupEntry(final Date date) throws NoSuchItemTypeException;
}
