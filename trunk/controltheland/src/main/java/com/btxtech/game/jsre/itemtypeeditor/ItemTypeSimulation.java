package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.MinimalService;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoveCommand;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DecoratorPanel;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 16.08.2011
 * Time: 12:28:30
 */
public class ItemTypeSimulation extends DecoratorPanel {
    private static final Id ID = new Id(0, 0, 0);
    private Context2d context2d;
    private ItemTypeImageLoader itemTypeImageLoader;
    private SurfaceImageLoader surfaceImageLoader;
    private SyncItem syncItem;
    private Logger log = Logger.getLogger(ItemTypeSimulation.class.getName());
    private int canvasWidth;
    private int canvasHeight;
    private int imageNr;
    private boolean doMove = false;

    public ItemTypeSimulation(int canvasWidth, int canvasHeight, ItemType itemType) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        Canvas canvas = Canvas.createIfSupported();
        if (canvas == null) {
            throw new IllegalStateException("ItemTypeEditorPanel: Canvas is not supported.");
        }
        canvas.setCoordinateSpaceWidth(canvasWidth);
        canvas.setCoordinateSpaceHeight(canvasHeight);
        setWidget(canvas);
        context2d = canvas.getContext2d();
        createSyncItem(itemType);

        final Timer timer = new Timer() {
            @Override
            public void run() {
                draw();
            }
        };
        itemTypeImageLoader = new ItemTypeImageLoader(itemType.getId(), itemType.getBoundingBox().getAngels().length, new ImageLoader.Listener() {
            @Override
            public void onLoaded() {
                timer.scheduleRepeating(40);
            }
        });
        surfaceImageLoader = new SurfaceImageLoader(23);
    }

    private void executeMoveCommand() {
        syncItem.getSyncItemArea().setPosition(new Index(canvasWidth / 2, canvasHeight / 2));

        Index middle = new Index(canvasWidth / 2, canvasHeight / 2);
        double angel = syncItem.getSyncItemArea().getBoundingBox().imageNumberToAngel(imageNr);
        Index destination = middle.getPointFromAngelToNord(angel, 200);

        MoveCommand moveCommand = new MoveCommand();
        moveCommand.setId(ID);
        moveCommand.setTimeStamp();
        List<Index> pathToDestination = new ArrayList<Index>();
        pathToDestination.add(syncItem.getSyncItemArea().getPosition());
        pathToDestination.add(destination);
        pathToDestination = correctPathAngel(pathToDestination, syncItem.getSyncItemArea().getBoundingBox());
        moveCommand.setPathToDestination(pathToDestination);
        SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
        try {
            syncBaseItem.executeCommand(moveCommand);
        } catch (Exception e) {
            log.log(Level.SEVERE, "", e);
        }
    }

    private void createSyncItem(ItemType itemType) {
        try {
            syncItem = new SyncBaseItem(ID, new Index(100, 100), (BaseItemType) itemType, MinimalService.getInstance(), null);
        } catch (NoSuchItemTypeException e) {
            log.log(Level.SEVERE, "", e);
        }
    }

    private void draw() {
        // Execute move
        SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
        if (doMove) {
            try {
                if (!syncBaseItem.tick(0.040)) {
                    executeMoveCommand();
                }
            } catch (Exception e) {
                log.log(Level.FINEST, "", e);
            }
        }
        // Draw background
        if (surfaceImageLoader.isLoaded()) {
            context2d.drawImage(surfaceImageLoader.getImage(0), 0, 0);
            context2d.drawImage(surfaceImageLoader.getImage(0), 200, 0);
            context2d.drawImage(surfaceImageLoader.getImage(0), 400, 0);
            context2d.drawImage(surfaceImageLoader.getImage(0), 0, 200);
            context2d.drawImage(surfaceImageLoader.getImage(0), 200, 200);
            context2d.drawImage(surfaceImageLoader.getImage(0), 400, 200);
            context2d.drawImage(surfaceImageLoader.getImage(0), 0, 400);
            context2d.drawImage(surfaceImageLoader.getImage(0), 200, 400);
            context2d.drawImage(surfaceImageLoader.getImage(0), 400, 400);
        }
        // Item
        if (itemTypeImageLoader.isLoaded()) {
            double angel = syncBaseItem.getSyncItemArea().getAngel();
            int imageNr = syncBaseItem.getSyncItemArea().getBoundingBox().angelToImageNr(angel);
            Index position = syncBaseItem.getSyncItemArea().getTopLeftFromImagePosition();
            context2d.drawImage(itemTypeImageLoader.getImage(imageNr), position.getX(), position.getY());
        }
    }

    private List<Index> correctPathAngel(List<Index> pathToDestination, BoundingBox boundingBox) {
        Index start = pathToDestination.get(0);
        Index end = pathToDestination.get(1);
        double distance = start.getDistance(end);
        double angel = start.getAngleToNord(end);
        double allowedAngel = boundingBox.getAllowedAngel(angel);
        Index newEnd = start.getPointFromAngelToNord(allowedAngel, distance);
        pathToDestination.set(1, newEnd);
        return pathToDestination;
    }

    /**
     * @param currentImage 1..x
     */
    public void setImageNumber(int currentImage) {
        double allowedAngel = syncItem.getSyncItemArea().getBoundingBox().imageNumberToAngel(currentImage - 1);
        syncItem.getSyncItemArea().setAngel(allowedAngel);
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
