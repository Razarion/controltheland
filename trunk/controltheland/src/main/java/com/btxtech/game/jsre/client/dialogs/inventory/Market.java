package com.btxtech.game.jsre.client.dialogs.inventory;

import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.Collection;

public class Market extends Composite implements HasText {
    private static MarketUiBinder uiBinder = GWT.create(MarketUiBinder.class);
    @UiField
    Button button;
    @UiField
    Label razarionAmount;
    @UiField
    FlowPanel artifactPlates;
    @UiField
    FlowPanel itemPlates;
    private MarketDialog marketDialog;
    private Collection<MarketItem> marketItems = new ArrayList<MarketItem>();
    private Collection<MarketArtifact> marketArtifacts = new ArrayList<MarketArtifact>();

    interface MarketUiBinder extends UiBinder<Widget, Market> {
    }

    public Market(MarketDialog marketDialog) {
        this.marketDialog = marketDialog;
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiHandler("button")
    void onClick(ClickEvent e) {
        marketDialog.close();
        DialogManager.showDialog(new InventoryDialog(), DialogManager.Type.QUEUE_ABLE);
    }

    public void setText(String text) {
    }

    public String getText() {
        return null;
    }

    public void setRazarionAmount(int amount) {
        razarionAmount.setText("Razarion: " + amount);
    }

    public void updateRazarion(int razarion) {
        setRazarionAmount(razarion);
        for (MarketItem marketItem : marketItems) {
            marketItem.handleButtonState(razarion);
        }
        for (MarketArtifact marketArtifact : marketArtifacts) {
            marketArtifact.handleButtonState(razarion);
        }
    }

    public void addMarketItem(InventoryItemInfo inventoryItemInfo, int razarion) {
        MarketItem marketItem = new MarketItem(inventoryItemInfo, razarion, marketDialog);
        marketItems.add(marketItem);
        itemPlates.add(marketItem);
    }

    public void addMarketArtifact(InventoryArtifactInfo inventoryArtifactInfo, int razarion) {
        MarketArtifact marketArtifact = new MarketArtifact(inventoryArtifactInfo, razarion, marketDialog);
        marketArtifacts.add(marketArtifact);
        itemPlates.add(marketArtifact);
    }

}
