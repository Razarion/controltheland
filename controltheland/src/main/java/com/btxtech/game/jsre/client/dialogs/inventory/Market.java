package com.btxtech.game.jsre.client.dialogs.inventory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlowPanel;

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

    interface MarketUiBinder extends UiBinder<Widget, Market> {
    }

    public Market() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiHandler("button")
    void onClick(ClickEvent e) {
        
    }

    public void setText(String text) {
    }

    public String getText() {
        return null;
    }

    public void setRazarionAmount(int amount) {
        razarionAmount.setText("Razarion: " + amount);
        
    }

    public void addMarketItem(InventoryItemInfo inventoryItemInfo) {
        itemPlates.add(new MarketItem(inventoryItemInfo));       
    }

    public void addMarketArtifact(InventoryArtifactInfo inventoryArtifactInfo) {
        itemPlates.add(new MarketArtifact(inventoryArtifactInfo));       
    }

}
