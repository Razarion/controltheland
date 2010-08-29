package com.btxtech.game.services.mgmt;

import com.btxtech.game.jsre.client.AlreadyUsedException;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ResourceType;
import com.btxtech.game.jsre.common.gameengine.services.items.ItemService;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.collision.CollisionService;
import com.btxtech.game.services.terrain.TerrainService;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * User: beat
 * Date: Jul 11, 2009
 * Time: 12:00:44 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:war/WEB-INF/applicationContext.xml"})
//@ContextConfiguration(locations = {"file:war/WEB-INF/applicationContext.xml"}, loader = TestWebSessionContextLoader.class)
//@TransactionConfiguration()
//@Transactional
public class TestMgmtService extends AbstractJUnit4SpringContextTests {
    public static final int ITEM_COUNT = 100000;
    @Autowired
    private MgmtService mgmtService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private BaseService baseService;
    @Autowired
    private CollisionService collisionService;
    @Autowired
    private TerrainService terrainService;

    @Test
    public void testBackup() throws AlreadyUsedException {
        mgmtService.backup();
    }

    @Test
    public void testBackupSummary() throws AlreadyUsedException {
        mgmtService.getBackupSummary();
    }

    @Test
    public void testRestore() throws AlreadyUsedException, NoSuchItemTypeException {
        List<BackupSummary> backupSummaries = mgmtService.getBackupSummary();
        mgmtService.restore(backupSummaries.get(0).getDate());
    }

    @Test
    public void testBigBackup() throws AlreadyUsedException, NoSuchItemTypeException {
        for (int i = 0; i < ITEM_COUNT; i++) {
            ItemType itemType = getRandomItemType();
            System.out.println("Creating: " + (i + 1) + " of " + ITEM_COUNT);
            itemService.createSyncObject(itemType, getRandomPosition(itemType), null, getBase(itemType), 0);
        }
        mgmtService.backup();
    }

    private SimpleBase getBase(ItemType itemType) throws AlreadyUsedException, NoSuchItemTypeException {
        if (itemType instanceof ResourceType) {
            return null;
        } else if (itemType instanceof BaseItemType) {
            return baseService.getBasesNoDummy().get(0).getSimpleBase();
        } else {
            throw new IllegalArgumentException("Unknown itemType: " + itemType);
        }
    }

    private Index getRandomPosition(ItemType itemType) {
        Rectangle rectangle = new Rectangle(0, 0, terrainService.getTerrainSettings().getPlayFieldXSize(), terrainService.getTerrainSettings().getPlayFieldYSize());
        return collisionService.getFreeRandomPosition(itemType, rectangle, 200);
    }

    public ItemType getRandomItemType() {
        int index = (int) (Math.random() * itemService.getItemTypes().size());
        return itemService.getItemTypes().get(index);
    }
}