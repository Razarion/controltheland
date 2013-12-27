package com.btxtech.game.jsre.client.unlock;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.ClientPlanetServices;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.ParametrisedRunnable;
import com.btxtech.game.jsre.client.dialogs.crystals.CrystalHelper;
import com.btxtech.game.jsre.client.dialogs.quest.QuestInfo;
import com.btxtech.game.jsre.client.dialogs.crystals.AffordableCallback;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.PlanetLiteInfo;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.gameengine.services.unlock.impl.UnlockContainer;
import com.btxtech.game.jsre.common.gameengine.services.unlock.impl.UnlockServiceImpl;

/**
 * User: beat
 * Date: 10.02.13
 * Time: 15:31
 */
public class ClientUnlockServiceImpl extends UnlockServiceImpl {
    private static final ClientUnlockServiceImpl INSTANCE = new ClientUnlockServiceImpl();
    private UnlockContainer unlockContainer;

    public static ClientUnlockServiceImpl getInstance() {
        return INSTANCE;
    }

    public boolean isItemLocked(BaseItemType baseItemType) {
        return isItemLocked(baseItemType, ClientBase.getInstance().getSimpleBase());
    }

    public boolean isQuestLocked(QuestInfo questInfo) {
        return super.isQuestLocked(questInfo, ClientBase.getInstance().getSimpleBase());
    }

    public boolean isPlanetLocked(PlanetLiteInfo planetLiteInfo) {
        return isPlanetLocked(planetLiteInfo, getUnlockContainer(ClientBase.getInstance().getSimpleBase()));
    }

    @Override
    public UnlockContainer getUnlockContainer(SimpleBase simpleBase) {
        if (unlockContainer == null) {
            throw new IllegalStateException("unlockContainer == null");
        }
        return unlockContainer;
    }

    @Override
    protected boolean isMission() {
        return Connection.getInstance().getGameEngineMode() != GameEngineMode.SLAVE;
    }

    @Override
    protected PlanetServices getPlanetServices(SimpleBase simpleBase) {
        return ClientPlanetServices.getInstance();
    }

    public void setUnlockContainer(UnlockContainer unlockContainer) {
        this.unlockContainer = unlockContainer;
    }

    public void askUnlockItem(final BaseItemType baseItemType, final Runnable successRunnable) {
        if (!baseItemType.isUnlockNeeded()) {
            throw new IllegalArgumentException("No unlock needed for base item type: " + baseItemType);
        }
        new CrystalHelper(ClientI18nHelper.CONSTANTS.unlockItemDialogTitle(),
                ClientI18nHelper.CONSTANTS.itemIsLocked(ClientI18nHelper.getLocalizedString(baseItemType.getI18Name())),
                ClientI18nHelper.CONSTANTS.unlockButton(),
                ClientI18nHelper.CONSTANTS.itemDialogNoCrystalsMessage(ClientI18nHelper.getLocalizedString(baseItemType.getI18Name()))) {

            @Override
            protected void askAffordable(final AffordableCallback affordableCallback) {
                Connection.getInstance().getCrystals(new ParametrisedRunnable<Integer>() {
                    @Override
                    public void run(Integer crystals) {
                        affordableCallback.onDetermined(baseItemType.getUnlockCrystals(), crystals);
                    }
                });
            }

            @Override
            protected void onBuy(int crystalCost, int crystalBalance) {
                Connection.getInstance().unlockItemType(baseItemType.getId(), successRunnable);
            }
        };
    }

    public void askUnlockQuest(final QuestInfo questInfo, final Runnable successRunnable) {
        if (!questInfo.isUnlockNeeded()) {
            throw new IllegalArgumentException("No unlock needed for questInfo: " + questInfo);
        }
        new CrystalHelper(ClientI18nHelper.CONSTANTS.unlockQuestDialogTitle(),
                ClientI18nHelper.CONSTANTS.questIsLocked(questInfo.getTitle()),
                ClientI18nHelper.CONSTANTS.unlockButton(),
                ClientI18nHelper.CONSTANTS.questDialogNoCrystalsMessage(questInfo.getTitle())) {

            @Override
            protected void askAffordable(final AffordableCallback affordableCallback) {
                Connection.getInstance().getCrystals(new ParametrisedRunnable<Integer>() {
                    @Override
                    public void run(Integer crystals) {
                        affordableCallback.onDetermined(questInfo.getUnlockCrystals(), crystals);
                    }
                });
            }

            @Override
            protected void onBuy(int crystalCost, int crystalBalance) {
                Connection.getInstance().unlockQuest(questInfo.getId(), successRunnable);
            }
        };
    }

    public void askUnlockPlanet(final PlanetLiteInfo planetLiteInfo, final Runnable successRunnable) {
        if (!planetLiteInfo.isUnlockNeeded()) {
            throw new IllegalArgumentException("No unlock needed for planetLiteInfo: " + planetLiteInfo);
        }

        new CrystalHelper(ClientI18nHelper.CONSTANTS.unlockPlanetDialogTitle(),
                ClientI18nHelper.CONSTANTS.planetIsLocked(planetLiteInfo.getName()),
                ClientI18nHelper.CONSTANTS.unlockButton(),
                ClientI18nHelper.CONSTANTS.planetDialogNoCrystalsMessage(planetLiteInfo.getName())) {

            @Override
            protected void askAffordable(final AffordableCallback affordableCallback) {
                Connection.getInstance().getCrystals(new ParametrisedRunnable<Integer>() {
                    @Override
                    public void run(Integer crystals) {
                        affordableCallback.onDetermined(planetLiteInfo.getUnlockCrystals(), crystals);
                    }
                });
            }

            @Override
            protected void onBuy(int crystalCost, int crystalBalance) {
                Connection.getInstance().unlockPlanet(planetLiteInfo, successRunnable);
            }
        };
    }

}
