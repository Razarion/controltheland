package com.btxtech.game.jsre.client.cockpit.item;

import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.cockpit.AbstractControlPanel;
import com.btxtech.game.jsre.client.cockpit.ChatCockpit;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.utg.ClientUserTracker;
import com.btxtech.game.jsre.client.utg.SpeechBubbleHandler;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat
 * Date: 08.11.2011
 * Time: 00:29:45
 */
public class ItemCockpit extends AbstractControlPanel implements BuildupItemPanel.BuildListener {
    private static final ItemCockpit INSTANCE = new ItemCockpit();
    private boolean isActive = false;
    private BuildupItemPanel buildupItemPanel;
    private SpecialFunctionPanel specialFunctionPanel;
    private AbsolutePanel panel;
    private FocusPanel excFocusPanel;

    public static ItemCockpit getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private ItemCockpit() {
        setup();
        getElement().getStyle().setZIndex(Constants.Z_INDEX_ITEM_COCKPIT);
        preventEvents();
    }

    public void activate(SyncBaseItem syncBaseItem) {
        if (Connection.getInstance().getGameEngineMode() == GameEngineMode.PLAYBACK) {
            return;
        }

        excFocusPanel.setFocus(true);
        buildupItemPanel.display(syncBaseItem);
        specialFunctionPanel.display(syncBaseItem);

        setVisible(true);
        Index relPosition = TerrainView.getInstance().toRelativeIndex(syncBaseItem.getSyncItemArea().getPosition());
        relPosition = relPosition.sub(getOffsetWidth() / 2, getOffsetHeight());
        if (relPosition.getX() < 0) {
            relPosition.setX(0);
        }
        if (relPosition.getY() < 0) {
            relPosition.setY(0);
        }
        if (relPosition.getX() + getOffsetWidth() > TerrainView.getInstance().getViewWidth()) {
            relPosition.setX(TerrainView.getInstance().getViewWidth() - getOffsetWidth());
        }
        if (relPosition.getY() + getOffsetHeight() > TerrainView.getInstance().getViewHeight()) {
            relPosition.setY(TerrainView.getInstance().getViewHeight() - getOffsetHeight());
        }

        panel.setWidgetPosition(this, relPosition.getX(), relPosition.getY());

        ClientUserTracker.getInstance().onDialogAppears(this, "ItemCockpit");
        SpeechBubbleHandler.getInstance().hide();

        isActive = true;
    }

    public void deActivate() {
        if (isActive) {
            ClientUserTracker.getInstance().onDialogDisappears(this);
            isActive = false;
            setVisible(false);
        }
    }

    public void addToParent(AbsolutePanel panel) {
        this.panel = panel;
        panel.add(this, 0, 0);
        setVisible(false);
    }

    @Override
    protected Widget createBody() {
        VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.getElement().getStyle().setColor("#C2D7EC");
        buildupItemPanel = new BuildupItemPanel(this);
        verticalPanel.add(buildupItemPanel);
        addEscKeyHandler(verticalPanel);
        specialFunctionPanel = new SpecialFunctionPanel();
        verticalPanel.add(specialFunctionPanel);
        return verticalPanel;
    }

    private void preventEvents() {
        getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
        addDomHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                GwtCommon.preventDefault(event);
                ChatCockpit.getInstance().blurFocus();
            }
        }, MouseUpEvent.getType());

        addDomHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                GwtCommon.preventDefault(event);
                ChatCockpit.getInstance().blurFocus();
            }
        }, MouseDownEvent.getType());
    }

    private void addEscKeyHandler(VerticalPanel verticalPanel) {
        excFocusPanel = new FocusPanel();
        excFocusPanel.setPixelSize(1, 1);
        excFocusPanel.getElement().getStyle().setBorderWidth(0, Style.Unit.PX);

        excFocusPanel.addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent keyDownEvent) {
                if (keyDownEvent.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
                    deActivate();
                }
            }
        });
        excFocusPanel.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                if (isActive) {
                    excFocusPanel.setFocus(true);
                }
            }
        });
        verticalPanel.add(excFocusPanel);
    }

    @Override
    public void onBuild() {
        deActivate();
    }

    public boolean isActive() {
        return isActive;
    }

    public void onMoneyChanged(double accountBalance) {
        buildupItemPanel.onMoneyChanged(accountBalance);
    }

    public void onStateChanged() {
        buildupItemPanel.onStateChanged();
    }

    public static boolean hasItemCockpit(SyncBaseItem syncBaseItem) {
        return syncBaseItem.hasSyncBuilder() ||
                syncBaseItem.hasSyncFactory() ||
                syncBaseItem.isUpgradeable() ||
                syncBaseItem.hasSyncItemContainer() ||
                syncBaseItem.hasSyncLauncher();
    }
}
