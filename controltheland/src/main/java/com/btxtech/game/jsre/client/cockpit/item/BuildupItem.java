package com.btxtech.game.jsre.client.cockpit.item;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.cockpit.CockpitMode;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.utg.ClientLevelHandler;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat Date: 08.04.2011 Time: 14:48:13
 */
public class BuildupItem extends Composite {
    private static BuildupItemUiBinder uiBinder = GWT.create(BuildupItemUiBinder.class);
    @UiField
    PushButton button;
    @UiField
    Label priceLabel;
    private EnableState enableState;
    private BaseItemType itemType;

    interface BuildupItemUiBinder extends UiBinder<Widget, BuildupItem> {
    }

    private enum EnableState {
        ENABLE(true, "Build", null), 
        DISABLED_LEVEL(false, "Build of", "not possible. Your are in the wrong level. Go to the next level!"), 
        DISABLED_LEVEL_EXCEEDED(false, "Build of",                "not possible. Item limit exceeded. Go to the next level!"), 
        DISABLED_HOUSE_SPACE_EXCEEDED(false, "Build of", "not possible. Item limit exceeded. Build more houses!"), 
        DISABLED_MONEY(false, "Build of", "not possible. Not enough money. Earn more money!");

        private boolean enabled;
        private String text1;
        private String text2;

        EnableState(boolean enabled, String text1, String text2) {
            this.enabled = enabled;
            this.text1 = text1;
            this.text2 = text2;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public String getToolTip(BaseItemType itemType) {
            StringBuilder builder = new StringBuilder();
            builder.append(text1);
            builder.append(" ");
            builder.append(itemType.getName());
            builder.append(" ");
            if (text2 != null) {
                builder.append(text2);
            }
            return builder.toString();
        }
    }

    public BuildupItem(BaseItemType itemType, MouseDownHandler mouseDownHandler) {
        initWidget(uiBinder.createAndBindUi(this));
        button.getUpFace().setImage(ImageHandler.getItemTypeImage(itemType, 40, 40));
        this.itemType = itemType;
        discoverEnableState();
        button.addMouseDownHandler(mouseDownHandler);
        button.addMouseMoveHandler(new MouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event) {
                if (event.getNativeButton() > 0 && CockpitMode.getInstance().hasToBeBuildPlacer()) {
                    // If mouse down events are going to this button
                    TerrainView.getInstance().getTerrainMouseHandler().onMouseMove(event);
                }
            }
        });
        priceLabel.setText("$" + itemType.getPrice());
        accomplishEnableState();
    }

    private void discoverEnableState() {
        if (!ClientLevelHandler.getInstance().isItemTypeAllowed(itemType)) {
            enableState = EnableState.DISABLED_LEVEL;
            return;
        }
        try {
            if (ClientBase.getInstance().isLevelLimitation4ItemTypeExceeded(itemType, ClientBase.getInstance().getSimpleBase())) {
                enableState = EnableState.DISABLED_LEVEL_EXCEEDED;
                return;
            }
            if (ClientBase.getInstance().isHouseSpaceExceeded(ClientBase.getInstance().getSimpleBase())) {
                enableState = EnableState.DISABLED_HOUSE_SPACE_EXCEEDED;
                return;
            }
        } catch (NoSuchItemTypeException e) {
            GwtCommon.handleException(e);
            enableState = EnableState.ENABLE;
            return;
        }
        if (itemType.getPrice() > ClientBase.getInstance().getAccountBalance()) {
            enableState = EnableState.DISABLED_MONEY;
            return;
        }
        enableState = EnableState.ENABLE;
    }

    private void accomplishEnableState() {
        setTitle(enableState.getToolTip(itemType));
        button.setEnabled(enableState.isEnabled());
    }

    public void onMoneyChanged(double accountBalance) {
        int price = itemType.getPrice();
        if (price > accountBalance && enableState == EnableState.ENABLE) {
            enableState = EnableState.DISABLED_MONEY;
            accomplishEnableState();
        } else if (price <= accountBalance && enableState == EnableState.DISABLED_MONEY) {
            discoverEnableState();
            accomplishEnableState();
        }
    }

    public void onStateChanged() {
        discoverEnableState();
        accomplishEnableState();
    }
}
