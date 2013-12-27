package com.btxtech.game.jsre.client.utg.tip.tiptask;

import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.item.ItemTypeContainer;
import com.btxtech.game.jsre.client.simulation.Simulation;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.utg.tip.GameTipManager;
import com.btxtech.game.jsre.client.utg.tip.visualization.GameTipVisualization;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;

/**
 * User: beat
 * Date: 22.08.12
 * Time: 13:19
 */
public abstract class AbstractTipTask implements MouseMoveHandler {
    private GameTipManager gameTipManager;
    private boolean conversionOnMouseMove;
    private HandlerRegistration handlerRegistration;
    private Timer poorConversionTimer;
    private long lastConversionTimeStamp;
    private Index lastMousePosition;

    public void setGameTipManager(GameTipManager gameTipManager) {
        this.gameTipManager = gameTipManager;
    }

    public abstract GameTipVisualization createInGameTip() throws NoSuchItemTypeException;

    public abstract boolean isFulfilled();

    protected abstract void internalStart();

    protected abstract void internalCleanup();

    public abstract String getTaskText();

    public void activateConversionOnMouseMove() {
        conversionOnMouseMove = true;
    }

    protected void onFailed() {
        cleanup();
        gameTipManager.onTaskFailed();
    }

    protected void onSucceed() {
        cleanup();
        gameTipManager.onSucceed();
    }

    public void start() {
        stopTimer();
        internalStart();
        if (conversionOnMouseMove) {
            lastMousePosition = null;
            handlerRegistration = MapWindow.getAbsolutePanel().addMouseMoveHandler(this);
            poorConversionTimer = new Timer() {
                @Override
                public void run() {
                    if (lastConversionTimeStamp > 0 && lastConversionTimeStamp + 1000 < System.currentTimeMillis()) {
                        lastConversionTimeStamp = 0;
                        Simulation.getInstance().onTipTaskPoorConversion();
                    }
                }
            };
            poorConversionTimer.scheduleRepeating(1000);
        }
    }

    public void cleanup() {
        internalCleanup();
        stopTimer();
        if (handlerRegistration != null) {
            handlerRegistration.removeHandler();
            handlerRegistration = null;
        }
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        //Logger.getLogger("xxx").warning("onMouseMove " + getClass().getName());
        if (lastMousePosition == null) {
            lastMousePosition = new Index(event.getX(), event.getY());
            return;
        }
        Index mousePosition = new Index(event.getX(), event.getY());
        if(mousePosition.getDistance(lastMousePosition) > 10)    {
            Simulation.getInstance().onTipTaskConversion();
            lastConversionTimeStamp = System.currentTimeMillis();
            lastMousePosition = mousePosition;
        }

  /*      onMouseMoveDebouncer++;
        if (onMouseMoveDebouncer > 10) {
            onMouseMoveDebouncer = 0;
            Simulation.getInstance().onTipTaskConversion();
            lastConversionTimeStamp = System.currentTimeMillis();
        } */
    }

    private void stopTimer() {
        if (poorConversionTimer != null) {
            poorConversionTimer.cancel();
            poorConversionTimer = null;
        }
    }

    protected String getItemTypeName(int itemTypeId) {
        try {
            return ClientI18nHelper.getLocalizedString(ItemTypeContainer.getInstance().getItemType(itemTypeId).getI18Name());
        } catch (NoSuchItemTypeException e) {
            ClientExceptionHandler.handleException("SelectTipTask.getTaskText()", e);
            return "Unit";
        }
    }
}
