package com.btxtech.game.jsre.client.utg;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.cockpit.item.ItemCockpit;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemListener;

/**
 * User: beat
 * Date: 12.11.2011
 * Time: 17:00:22
 */
public class SpeechBubbleHandler implements SyncItemListener {
    private static final SpeechBubbleHandler INSTANCE = new SpeechBubbleHandler();
    private SpeechBubble itemSpeechBubble;
    private Index positionOrigin;
    private SyncBaseItem syncBaseItem;

    public static SpeechBubbleHandler getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private SpeechBubbleHandler() {
    }

    public void show(SyncItem syncItem) {
        hide();
        itemSpeechBubble = new SpeechBubble(syncItem, setupHtml(syncItem), true);
        itemSpeechBubble.setBgColor(setupColor(syncItem));
        Index position = syncItem.getSyncItemArea().getPosition();
        TerrainView.getInstance().toAbsoluteIndex(position);
        addMoveListener(syncItem);
    }

    public void hide() {
        if (itemSpeechBubble != null) {
            itemSpeechBubble.close();
            itemSpeechBubble = null;
            removeMoveListener();
        }
    }

    private String setupColor(SyncItem syncItem) {
        if (syncItem instanceof SyncBaseItem) {
            SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
            if (ClientBase.getInstance().isMyOwnProperty(syncBaseItem)) {
                return "#b2ffb2";
            } else {
                return "#ffa6a6";
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
                builder.append(syncBaseItem.getBaseItemType().getDescription());
                if (ItemCockpit.hasItemCockpit(syncBaseItem) || syncBaseItem.hasSyncMovable()) {
                    builder.append("</br><b>Click on it!</b>");
                }
            } else {
                builder.append("Attack this <b>enemy</b> unit!");
                builder.append("</br>");
                builder.append(ClientBase.getInstance().getBaseName(syncBaseItem.getBase()));
            }
        } else {
            builder.append("Gather <b>money</b> from this gold.");
        }
        return builder.toString();
    }

    private void addMoveListener(SyncItem syncItem) {
        if (syncItem instanceof SyncBaseItem) {
            SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
            if (syncBaseItem.hasSyncMovable()) {
                syncBaseItem.addSyncItemListener(this);
                positionOrigin = syncItem.getSyncItemArea().getPosition();

            }
        }
    }

    private void removeMoveListener() {
        if (syncBaseItem != null) {
            syncBaseItem.removeSyncItemListener(this);
            syncBaseItem = null;
        }
    }

    @Override
    public void onItemChanged(Change change, SyncItem syncItem) {
        if (change == Change.POSITION) {
            if (syncItem.getSyncItemArea().getPosition().getDistance(positionOrigin) > 100) {
                hide();
            }
        }
    }
}
