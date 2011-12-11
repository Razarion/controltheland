package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemListener;
import com.btxtech.game.jsre.common.tutorial.ItemTypeAndPosition;
import com.google.gwt.core.client.Scheduler;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 16.08.2011
 * Time: 12:28:30
 */
public class ItemTypeSimulation {
    private SyncItem syncItem;
    private Logger log = Logger.getLogger(ItemTypeSimulation.class.getName());
    private int canvasWidth;
    private int canvasHeight;
    private ItemType itemType;
    private boolean doMove = false;
    private int imageNr = 0;
    private Index destination;
    private MuzzleFlashControl muzzleFlashControl;

    public ItemTypeSimulation(int canvasWidth, int canvasHeight, ItemType itemType, MuzzleFlashControl muzzleFlashControl) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        this.itemType = itemType;
        this.muzzleFlashControl = muzzleFlashControl;
    }

    private void executeMoveCommand() {
        syncItem.getSyncItemArea().setPosition(new Index(canvasWidth / 2, canvasHeight / 2));
        double angel = syncItem.getSyncItemArea().getBoundingBox().imageNumberToAngel(imageNr);
        Index middle = new Index(canvasWidth / 2, canvasHeight / 2);
        destination = middle.getPointFromAngelToNord(angel, 200);
        ActionHandler.getInstance().move((SyncBaseItem) syncItem, destination);
    }

    public void createSyncItem() {
        try {
            Index middle = new Index(canvasWidth / 2, canvasHeight / 2);
            syncItem = ItemContainer.getInstance().createSimulationSyncObject(new ItemTypeAndPosition(ItemTypeEditorPanel.MY_BASE, 0, itemType.getId(), middle, 0));
            syncItem.addSyncItemListener(new SyncItemListener() {
                @Override
                public void onItemChanged(Change change, SyncItem syncItem) {
                    if (change == Change.POSITION && destination != null && destination.equals(syncItem.getSyncItemArea().getPosition())) {
                        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                            @Override
                            public void execute() {
                                executeMoveCommand();
                            }
                        });
                    }
                }
            });
            if (syncItem instanceof SyncBaseItem) {
                muzzleFlashControl.initSyncItem((SyncBaseItem) syncItem);
            }


        } catch (Exception e) {
            log.log(Level.SEVERE, "", e);
        }
    }

    public void onImageChanged(int imageNr) {
        this.imageNr = imageNr;
        if (doMove) {
            executeMoveCommand();
        }
    }

    public void doMove(boolean value) {
        doMove = value && (syncItem instanceof SyncBaseItem && ((SyncBaseItem) syncItem).hasSyncMovable());
    }

    public SyncItem getSyncItem() {
        return syncItem;
    }
}
