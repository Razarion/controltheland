package com.btxtech.game.services.mgmt;

import com.btxtech.game.services.TestWebSessionContextLoader;
import com.btxtech.game.services.base.AlreadyUsedException;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: beat
 * Date: Jul 11, 2009
 * Time: 12:00:44 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:war/WEB-INF/applicationContext.xml"}, loader= TestWebSessionContextLoader.class)
@TransactionConfiguration()
@Transactional
public class TestMgmtService extends AbstractJUnit4SpringContextTests {
    @Autowired
    private MgmtService mgmtService;

    @Test
    public void testBackup() throws AlreadyUsedException {
        mgmtService.backup();
    }

    @Test
    public void testBackupSummary() throws AlreadyUsedException {
        mgmtService.getBackupSummary();
    }

    @Test
    public void testRestore() throws AlreadyUsedException {
        List<BackupSummary> backupSummaries =  mgmtService.getBackupSummary();
        mgmtService.restore(backupSummaries.get(0).getDate());
    }


}