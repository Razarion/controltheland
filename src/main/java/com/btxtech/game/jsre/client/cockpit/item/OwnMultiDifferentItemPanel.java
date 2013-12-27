package com.btxtech.game.jsre.client.cockpit.item;

import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;
import java.util.Map;

public class OwnMultiDifferentItemPanel extends Composite {
    private static final int SCROLL_STEP = 20;

    private static OwnMultiDifferentItemPanelUiBinder uiBinder = GWT.create(OwnMultiDifferentItemPanelUiBinder.class);
    @UiField
    PushButton scrollLeftButton;
    @UiField
    PushButton scrollRightButton;
    @UiField
    ScrollPanel scrollPanel;

    interface OwnMultiDifferentItemPanelUiBinder extends UiBinder<Widget, OwnMultiDifferentItemPanel> {
    }

    public OwnMultiDifferentItemPanel(Map<BaseItemType, Collection<SyncBaseItem>> itemTypes) {
        initWidget(uiBinder.createAndBindUi(this));
        HorizontalPanel selectedItemTypes = new HorizontalPanel();
        for (Map.Entry<BaseItemType, Collection<SyncBaseItem>> entry : itemTypes.entrySet()) {
            selectedItemTypes.add(new SelectedItemType(entry.getKey(), entry.getValue().size()));
        }
        scrollPanel.setWidget(selectedItemTypes);
        scrollPanel.scrollToLeft();
    }

    @UiHandler("scrollLeftButton")
    void onScrollLeftButtonClick(ClickEvent event) {
        scrollPanel.setHorizontalScrollPosition(scrollPanel.getHorizontalScrollPosition() - SCROLL_STEP);
    }

    @UiHandler("scrollRightButton")
    void onScrollRightButtonClick(ClickEvent event) {
        scrollPanel.setHorizontalScrollPosition(scrollPanel.getHorizontalScrollPosition() + SCROLL_STEP);
    }
}
