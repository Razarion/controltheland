package com.btxtech.game.jsre.client.utg;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.cockpit.menu.MenuBarCockpit;
import com.btxtech.game.jsre.client.dialogs.DeadEndDialog;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.item.ItemTypeContainer;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.items.ItemService;
import com.btxtech.game.jsre.common.gameengine.services.items.ItemTypeService;

/**
 * User: beat
 * Date: 22.04.13
 * Time: 14:34
 */
public class ClientDeadEndProtection extends DeadEndProtection implements DeadEndListener {
    private static final int DIALOG_PERIOD = 15000;
    private static final ClientDeadEndProtection INSTANCE = new ClientDeadEndProtection();
    private DeadEndDialog itemDeadEndDialog = new DeadEndDialog(ClientI18nHelper.CONSTANTS.reachedDeadEndItem());
    private DeadEndDialog moneyDeadEndDialog = new DeadEndDialog(ClientI18nHelper.CONSTANTS.reachedDeadEndMoney());

    /**
     * Singleton
     */
    private ClientDeadEndProtection() {
        setDeadEndListener(this);
    }

    public static ClientDeadEndProtection getInstance() {
        return INSTANCE;
    }

    @Override
    protected ItemService getItemService() {
        return ItemContainer.getInstance();
    }

    @Override
    protected ItemTypeService getItemTypeService() {
        return ItemTypeContainer.getInstance();
    }

    @Override
    protected SimpleBase getMyBase() {
        return ClientBase.getInstance().getSimpleBase();
    }

    @Override
    protected boolean isBaseDead() {
        return ClientBase.getInstance().isBaseDead();
    }

    @Override
    protected int getMyMoney() {
        return (int) ClientBase.getInstance().getAccountBalance();
    }

    @Override
    protected boolean isSuppressed() {
        return Connection.getInstance().getGameEngineMode() != GameEngineMode.SLAVE;
    }

    @Override
    public void activateItemDeadEnd() {
        itemDeadEndDialog.start(true, DIALOG_PERIOD);
        MenuBarCockpit.getInstance().blinkNewBase(true);
    }

    @Override
    public void revokeItemDeadEnd() {
        itemDeadEndDialog.stop();
        MenuBarCockpit.getInstance().blinkNewBase(false);
    }

    @Override
    public void activateMoneyDeadEnd() {
        moneyDeadEndDialog.start(true, DIALOG_PERIOD);
        MenuBarCockpit.getInstance().blinkNewBase(true);
    }

    @Override
    public void revokeMoneyDeadEnd() {
        moneyDeadEndDialog.stop();
        MenuBarCockpit.getInstance().blinkNewBase(false);
    }
}
