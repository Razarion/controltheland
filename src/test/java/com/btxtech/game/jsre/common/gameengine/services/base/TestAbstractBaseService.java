package com.btxtech.game.jsre.common.gameengine.services.base;

import com.btxtech.game.jsre.client.common.NotYourBaseException;
import com.btxtech.game.jsre.client.common.info.SimpleGuild;
import com.btxtech.game.jsre.common.InsufficientFundsException;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.GlobalServices;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.gameengine.services.base.impl.AbstractBaseServiceImpl;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseObject;
import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: beat
 * Date: 03.05.12
 * Time: 12:54
 */
public class TestAbstractBaseService {

    @Test
    public void isEnemy() {
        Collection<BaseAttributes> allBaseAttributes = new ArrayList<>();
        SimpleBase bot1 = new SimpleBase(1, 1);
        BaseAttributes baseAttributesBot1 = new BaseAttributes(bot1, "bot1", false, null);
        baseAttributesBot1.setBot(true);
        allBaseAttributes.add(baseAttributesBot1);

        SimpleBase bot2 = new SimpleBase(2, 1);
        BaseAttributes baseAttributesBot2 = new BaseAttributes(bot2, "bot2", false, null);
        baseAttributesBot2.setBot(true);
        allBaseAttributes.add(baseAttributesBot2);

        SimpleBase base1 = new SimpleBase(3, 1);
        BaseAttributes baseAttributes1 = new BaseAttributes(base1, "base1", false, new SimpleGuild(1, null));
        allBaseAttributes.add(baseAttributes1);

        SimpleBase base2 = new SimpleBase(4, 1);
        BaseAttributes baseAttributes2 = new BaseAttributes(base2, "base2", false, new SimpleGuild(1, null));
        allBaseAttributes.add(baseAttributes2);

        SimpleBase base3 = new SimpleBase(5, 1);
        BaseAttributes baseAttributes3 = new BaseAttributes(base3, "base3", false, null);
        allBaseAttributes.add(baseAttributes3);

        TestAbstractBaseServiceImpl abstractBaseService = new TestAbstractBaseServiceImpl();
        abstractBaseService.setAllBaseAttributes(allBaseAttributes);

        Assert.assertFalse(abstractBaseService.isEnemy(bot1, bot2));
        Assert.assertFalse(abstractBaseService.isEnemy(bot2, bot1));

        Assert.assertTrue(abstractBaseService.isEnemy(base1, bot1));
        Assert.assertTrue(abstractBaseService.isEnemy(base2, bot1));
        Assert.assertTrue(abstractBaseService.isEnemy(base3, bot1));
        Assert.assertTrue(abstractBaseService.isEnemy(base1, bot2));
        Assert.assertTrue(abstractBaseService.isEnemy(base2, bot2));
        Assert.assertTrue(abstractBaseService.isEnemy(base3, bot2));
        Assert.assertTrue(abstractBaseService.isEnemy(bot1, base1));
        Assert.assertTrue(abstractBaseService.isEnemy(bot1, base2));
        Assert.assertTrue(abstractBaseService.isEnemy(bot1, base3));
        Assert.assertTrue(abstractBaseService.isEnemy(bot2, base1));
        Assert.assertTrue(abstractBaseService.isEnemy(bot2, base2));
        Assert.assertTrue(abstractBaseService.isEnemy(bot2, base3));

        Assert.assertFalse(abstractBaseService.isEnemy(base1, base1));
        Assert.assertFalse(abstractBaseService.isEnemy(base1, base2));
        Assert.assertTrue(abstractBaseService.isEnemy(base1, base3));
        Assert.assertFalse(abstractBaseService.isEnemy(base1, base1));
        Assert.assertFalse(abstractBaseService.isEnemy(base2, base1));
        Assert.assertTrue(abstractBaseService.isEnemy(base3, base1));

        Assert.assertFalse(abstractBaseService.isEnemy(base2, base1));
        Assert.assertFalse(abstractBaseService.isEnemy(base2, base2));
        Assert.assertTrue(abstractBaseService.isEnemy(base2, base3));
        Assert.assertFalse(abstractBaseService.isEnemy(base1, base2));
        Assert.assertFalse(abstractBaseService.isEnemy(base2, base2));
        Assert.assertTrue(abstractBaseService.isEnemy(base3, base2));

        Assert.assertTrue(abstractBaseService.isEnemy(base3, base1));
        Assert.assertTrue(abstractBaseService.isEnemy(base3, base2));
        Assert.assertFalse(abstractBaseService.isEnemy(base3, base3));
        Assert.assertTrue(abstractBaseService.isEnemy(base1, base3));
        Assert.assertTrue(abstractBaseService.isEnemy(base2, base3));
        Assert.assertFalse(abstractBaseService.isEnemy(base3, base3));
    }

    class TestAbstractBaseServiceImpl extends AbstractBaseServiceImpl {
        @Override
        public void depositResource(double price, SimpleBase simpleBase) {
        }

        @Override
        public void withdrawalMoney(double price, SimpleBase simpleBase) throws InsufficientFundsException {
        }

        @Override
        public int getHouseSpace(SimpleBase simpleBase) {
            return 0;
        }

        @Override
        public int getUsedHouseSpace(SimpleBase simpleBase) {
            return 0;
        }

        @Override
        public int getItemCount(SimpleBase simpleBase, int itemTypeId) throws NoSuchItemTypeException {
            return 0;
        }

        @Override
        public boolean isItemLimit4ItemAddingAllowed(BaseItemType newItemType, SimpleBase simpleBase) throws NoSuchItemTypeException {
            return false;
        }

        @Override
        public SimpleBase createBotBase(BotConfig botConfig) {
            return null;
        }

        @Override
        public Collection<SyncBaseItem> getItems(SimpleBase simpleBase) {
            return null;
        }

        @Override
        public void checkBaseAccess(SyncBaseItem syncBaseItem) throws NotYourBaseException {
        }

        @Override
        public void sendAccountBaseUpdate(SimpleBase simpleBase) {
        }

        @Override
        public void sendAccountBaseUpdate(SyncBaseObject syncBaseObject) {
        }

        @Override
        public void onItemCreated(SyncBaseItem syncBaseItem) {
        }

        @Override
        public void onItemDeleted(SyncBaseItem syncBaseItem, SimpleBase actor) {
        }

        @Override
        protected GlobalServices getGlobalServices() {
            return null;
        }

        @Override
        protected PlanetServices getPlanetServices() {
            return null;
        }
    }

}
