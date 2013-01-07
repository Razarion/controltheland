package com.btxtech.game.jsre.client.dialogs.inventory;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.ClientPlanetServices;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.dialogs.Dialog;
import com.google.gwt.user.client.ui.VerticalPanel;

import java.util.logging.Logger;

/**
 * User: beat
 * Date: 26.05.12
 * Time: 00:05
 */
public class InventoryDialog extends Dialog {
    private static InventoryDialog staticInstance;
    private Logger log = Logger.getLogger(InventoryDialog.class.getName());
    private MainTabbedPanel mainTabbedPanel;
    private Integer filterPlanetId;
    private boolean filterLevel;
    private boolean isCenterCalledOnce;

    public InventoryDialog() {
        super(ClientI18nHelper.CONSTANTS.inventory());
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        mainTabbedPanel = new MainTabbedPanel(this);
        dialogVPanel.add(mainTabbedPanel);
        filterPlanetId = ClientPlanetServices.getInstance().getPlanetInfo().getPlanetId();
        filterLevel = true;
        Connection.getInstance().loadInventory(filterPlanetId, filterLevel, this);
        staticInstance = this;
    }

    public void onItemsReceived(InventoryInfo inventoryInfo) {
        if (inventoryInfo == null) {
            log.warning("InventoryDialog.onItemsReceived() inventoryInfo == null");
            return;
        }
        mainTabbedPanel.setRazarionAmount(inventoryInfo.getRazarion());
        mainTabbedPanel.displayInventory(inventoryInfo.getOwnInventoryItems());
        mainTabbedPanel.displayWorkshop(inventoryInfo, inventoryInfo.getAllInventoryItemInfos());
        mainTabbedPanel.displayDealerItems(inventoryInfo, inventoryInfo.getAllInventoryItemInfos());
        mainTabbedPanel.displayDealerFunds(inventoryInfo, inventoryInfo.getAllInventoryItemInfos());
        mainTabbedPanel.displayDealerArtifacts(inventoryInfo, inventoryInfo.getAllInventoryArtifactInfos());

        if (!isCenterCalledOnce) {
            center();
            isCenterCalledOnce = true;
        }
    }

    @Override
    public void close() {
        super.close();
        staticInstance = null;
    }

    public static void onBoxPicket() {
        if (staticInstance != null) {
            Connection.getInstance().loadInventory(staticInstance.filterPlanetId, staticInstance.filterLevel, staticInstance);
        }
    }

    public void setFilter(Integer filterPlanetId, boolean filterLevel) {
        this.filterPlanetId = filterPlanetId;
        this.filterLevel = filterLevel;
    }

    public void reload() {
        Connection.getInstance().loadInventory(staticInstance.filterPlanetId, staticInstance.filterLevel, staticInstance);
    }

    public Integer getFilterPlanetId() {
        return filterPlanetId;
    }

    public boolean isFilterLevel() {
        return filterLevel;
    }
}
