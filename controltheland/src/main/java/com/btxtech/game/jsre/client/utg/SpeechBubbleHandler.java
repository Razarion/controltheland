package com.btxtech.game.jsre.client.utg;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.cockpit.item.ItemCockpit;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.google.gwt.user.client.Timer;

/**
 * User: beat
 * Date: 12.11.2011
 * Time: 17:00:22
 */
public class SpeechBubbleHandler {
    private static final int DEFERRED_SHOW = 500;
    public static boolean uglySuppress = false;
    private static final SpeechBubbleHandler INSTANCE = new SpeechBubbleHandler();
    private SpeechBubble itemSpeechBubble;
    private SyncItem deferredSyncItem;
    private boolean mouseOverSpeechBubble = false;
    private boolean mouseOverItemType = false;
    private Timer deferredShowTimer;
    private Timer deferredHideTimer;

    public static SpeechBubbleHandler getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private SpeechBubbleHandler() {
    }

    public void show(SyncItem syncItem) {
        if (uglySuppress) {
            return;
        }
        if(ItemCockpit.getInstance().isActive()) {
            return;
        }
        mouseOverItemType = true;
        if (syncItem.equals(deferredSyncItem)) {
            return;
        }
        hide();
        startDeferredShow();
        deferredSyncItem = syncItem;
        mouseOverSpeechBubble = false;
    }

    private void startDeferredShow() {
        stopDeferredShow();
        deferredShowTimer = new Timer() {
            @Override
            public void run() {
                display();
                deferredShowTimer = null;
            }
        };
        deferredShowTimer.schedule(DEFERRED_SHOW);
    }

    private void stopDeferredShow() {
        if (deferredShowTimer != null) {
            deferredShowTimer.cancel();
            deferredShowTimer = null;
        }
    }

    private void display() {
        itemSpeechBubble = new SpeechBubble(deferredSyncItem, setupHtml(deferredSyncItem), true);
        itemSpeechBubble.setBgColor(setupColor(deferredSyncItem));
        Index position = deferredSyncItem.getSyncItemArea().getPosition();
        TerrainView.getInstance().toAbsoluteIndex(position);
    }

    public void hide() {
        stopDeferredShow();
        stopDeferredHide();
        if (itemSpeechBubble != null) {
            itemSpeechBubble.close();
            itemSpeechBubble = null;
        }
        deferredSyncItem = null;
        mouseOverItemType = false;
        mouseOverSpeechBubble = false;
    }

    private String setupColor(SyncItem syncItem) {
        if (syncItem instanceof SyncBaseItem) {
            SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
            if (ClientBase.getInstance().isMyOwnProperty(syncBaseItem)) {
                return "#b2ffb2";
            } else if (ClientBase.getInstance().isEnemy(syncBaseItem)) {
                return "#ffa6a6";
            } else {
                return "#b2ffb2";
            }
        } else {
            return "#FFFFFF";
        }
    }

    private String setupHtml(SyncItem syncItem) {
        StringBuilder builder = new StringBuilder();
        if (syncItem instanceof SyncBaseItem) {
            SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
            if (ClientBase.getInstance().isMyOwnProperty(syncBaseItem)) {
                builder.append("<b>");
                builder.append(syncItem.getItemType().getName());
                builder.append("</b>");
                builder.append("<br />");
                builder.append(syncBaseItem.getBaseItemType().getDescription());
                if (ItemCockpit.hasItemCockpit(syncBaseItem) || syncBaseItem.hasSyncMovable()) {
                    builder.append("<br /><b>Click on it!</b>");
                }
            } else if (ClientBase.getInstance().isBot(syncBaseItem.getBase())) {
                builder.append("Attack this <b>enemy</b>!<br />");
                builder.append("<b>");
                builder.append(syncItem.getItemType().getName());
                builder.append("</b>");
                builder.append("<br />");
                builder.append(ClientBase.getInstance().getBaseName(syncBaseItem.getBase()));
            } else if (ClientBase.getInstance().isEnemy(syncBaseItem)) {
                builder.append("Attack this <b>enemy</b>!<br />");
                builder.append("<b>");
                builder.append(syncItem.getItemType().getName());
                builder.append("</b>");
                builder.append("<br />");
                builder.append(ClientBase.getInstance().getBaseName(syncBaseItem.getBase()));
                builder.append("<br /><a href=\"javascript:offerAlliance(");
                builder.append(syncBaseItem.getBase().getId());
                builder.append(");\">Offer alliance</a>");
            } else {
                builder.append("Alliance member ");
                builder.append("<br />");
                builder.append("<b>");
                builder.append(syncItem.getItemType().getName());
                builder.append("</b>");
                builder.append("<br />");
                builder.append(ClientBase.getInstance().getBaseName(syncBaseItem.getBase()));
            }
        } else {
            builder.append("<b>");
            builder.append(syncItem.getItemType().getName());
            builder.append("</b>");
            builder.append("<br />");
            builder.append(syncItem.getItemType().getDescription());
        }
        return builder.toString();
    }

    public void onSpeechBubbleMouseOver() {
        mouseOverSpeechBubble = true;
    }

    public void onSpeechBubbleMouseOut() {
        mouseOverSpeechBubble = false;
        if (itemSpeechBubble != null) {
            deferredClose();
        } else {
            stopDeferredShow();
            deferredSyncItem = null;
        }
    }

    public void onSyncItemMouseOut(SyncItem syncItem) {
        if (syncItem.equals(deferredSyncItem)) {
            if (itemSpeechBubble != null) {
                deferredClose();
            } else {
                stopDeferredShow();
                deferredSyncItem = null;
            }
            mouseOverItemType = false;
        }
    }

    private void deferredClose() {
        stopDeferredHide();
        deferredHideTimer = new Timer() {
            @Override
            public void run() {
                if (!mouseOverSpeechBubble && !mouseOverItemType) {
                    hide();
                }
            }
        };
        deferredHideTimer.schedule(500);
    }

    private void stopDeferredHide() {
        if (deferredHideTimer != null) {
            deferredHideTimer.cancel();
            deferredHideTimer = null;
        }
    }

    public void itemKilled(SyncItem syncItem) {
        if (syncItem.equals(deferredSyncItem)) {
            stopDeferredShow();
            hide();
        }
    }
}
