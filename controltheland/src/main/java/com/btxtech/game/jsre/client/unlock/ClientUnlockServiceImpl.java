package com.btxtech.game.jsre.client.unlock;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.ClientPlanetServices;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.ParametrisedRunnable;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.UnlockDialog;
import com.btxtech.game.jsre.client.dialogs.YesNoDialog;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryDialog;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.gameengine.services.unlock.impl.UnlockContainer;
import com.btxtech.game.jsre.common.gameengine.services.unlock.impl.UnlockServiceImpl;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

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
        Connection.getInstance().getRazarion(new ParametrisedRunnable<Integer>() {
            @Override
            public void run(Integer razarion) {
                if (baseItemType.getUnlockRazarion() > razarion) {
                    DialogManager.showDialog(new YesNoDialog(ClientI18nHelper.CONSTANTS.unlockDialogTitle(),
                            ClientI18nHelper.CONSTANTS.itemDialogNoRazarionMessage(ClientI18nHelper.getLocalizedString(baseItemType.getI18Name()), baseItemType.getUnlockRazarion(), razarion),
                            ClientI18nHelper.CONSTANTS.buy(),
                            new ClickHandler() {
                                @Override
                                public void onClick(ClickEvent event) {
                                    DialogManager.showDialog(new InventoryDialog(true), DialogManager.Type.PROMPTLY);
                                }
                            },
                            ClientI18nHelper.CONSTANTS.close(),
                            null),
                            DialogManager.Type.PROMPTLY);
                } else {
                    DialogManager.showDialog(new UnlockDialog(baseItemType, razarion, successRunnable), DialogManager.Type.PROMPTLY);
                }
            }
        });
    }

}
