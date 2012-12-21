package com.btxtech.game.jsre.client.cockpit.item;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.event.dom.client.MouseDownEvent;

/**
 * User: beat Date: 08.04.2011 Time: 14:48:13
 */
public class BuildupItem extends Composite {
    private static BuildupItemUiBinder uiBinder = GWT.create(BuildupItemUiBinder.class);
    @UiField
    Label priceLabel;
    @UiField
    Button button;
    private EnableState enableState;
    private BaseItemType itemType;
    private BuildupItemButtonContent buildupItemButtonContent;
    private int itemCount;
    private int itemLimit;
    private ButtonListener buttonListener;

    interface BuildupItemUiBinder extends UiBinder<Widget, BuildupItem> {
    }

    public interface ButtonListener {
        void onButtonPressed(Index relativeMousePosition);
    }

    private enum EnableState {
        ENABLE(true, "Build", null),
        DISABLED_LEVEL(false, "Build of", "not possible. Your are in the wrong level. Go to the next level!"),
        DISABLED_LEVEL_EXCEEDED(false, "Build of", "not possible. Item limit exceeded. Go to the next level!"),
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

    public BuildupItem(BaseItemType itemType, final ButtonListener buttonListener) {
        initWidget(uiBinder.createAndBindUi(this));
        this.itemType = itemType;
        this.buttonListener = buttonListener;
        discoverEnableState();
        buildupItemButtonContent = new BuildupItemButtonContent(itemType);
        button.getElement().appendChild(buildupItemButtonContent.getElement());
        priceLabel.setText("$" + itemType.getPrice());
        accomplishEnableState();
    }

    private void discoverEnableState() {
        try {
            itemCount = ClientBase.getInstance().getItemCount(ClientBase.getInstance().getSimpleBase(), itemType.getId());
            itemLimit = ClientBase.getInstance().getLimitation4ItemType(ClientBase.getInstance().getSimpleBase(), itemType);
            if (ClientBase.getInstance().isLevelLimitation4ItemTypeExceeded(itemType, ClientBase.getInstance().getSimpleBase())) {
                enableState = EnableState.DISABLED_LEVEL_EXCEEDED;
                return;
            }
            if (ClientBase.getInstance().isHouseSpaceExceeded(ClientBase.getInstance().getSimpleBase(), itemType)) {
                enableState = EnableState.DISABLED_HOUSE_SPACE_EXCEEDED;
                return;
            }
        } catch (NoSuchItemTypeException e) {
            ClientExceptionHandler.handleException(e);
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
        buildupItemButtonContent.setEnabled(enableState.isEnabled());
        buildupItemButtonContent.setText(itemCount + "/" + itemLimit);
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

    @UiHandler("button")
    void onButtonMouseDown(MouseDownEvent event) {
        buttonListener.onButtonPressed(new Index(event.getX(), event.getY()));
        GwtCommon.preventDefault(event);
    }
}
