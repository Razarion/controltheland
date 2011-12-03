package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Line;
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
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
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
    private ImageLoader imageLoader;
    private CssColor redrawColor = CssColor.make(255, 255, 255);
    private int canvasWidth;
    private int canvasHeight;
    private BoundingBoxControl boundingBoxControl;
    private SyncItem syncItem;
    private Logger log = Logger.getLogger(ItemTypeSimulation.class.getName());
    private Line line;
    private Index oldPosition;

    public ItemTypeSimulation(int canvasWidth, int canvasHeight, ItemType itemType, BoundingBoxControl boundingBoxControl) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        this.boundingBoxControl = boundingBoxControl;
        Canvas canvas = Canvas.createIfSupported();
        if (canvas == null) {
            throw new IllegalStateException("ItemTypeEditorPanel: Canvas is not supported.");
        }
        canvas.setCoordinateSpaceWidth(canvasWidth);
        canvas.setCoordinateSpaceHeight(canvasHeight);
        setWidget(canvas);
        canvas.setCoordinateSpaceWidth(canvasWidth);
        canvas.setCoordinateSpaceHeight(canvasHeight);
        canvas.addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                executeMoveCommand(new Index(event.getX(), event.getY()));
            }
        });
        context2d = canvas.getContext2d();
        createSyncItem(itemType);

        final Timer timer = new Timer() {
            @Override
            public void run() {
                move();
            }
        };
        imageLoader = new ImageLoader(itemType.getId(), itemType.getBoundingBox().getAngels().length, new ImageLoader.Listener() {
            @Override
            public void onLoaded() {
                timer.scheduleRepeating(40);
            }
        });
    }

    private void executeMoveCommand(Index moveTo) {
        oldPosition = syncItem.getSyncItemArea().getPosition();
        MoveCommand moveCommand = new MoveCommand();
        moveCommand.setId(ID);
        moveCommand.setTimeStamp();
        List<Index> pathToDestination = new ArrayList<Index>();
        pathToDestination.add(syncItem.getSyncItemArea().getPosition());
        pathToDestination.add(moveTo);
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

    private void move() {
        SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
        try {
            if (!syncBaseItem.tick(0.040)) {
                if (oldPosition != null) {
                    executeMoveCommand(oldPosition);
                }
            }
        } catch (Exception e) {
            log.log(Level.FINEST, "", e);
        }

        if (!imageLoader.isLoaded()) {
            return;
        }
        context2d.setFillStyle(redrawColor);
        context2d.fillRect(0, 0, canvasWidth, canvasHeight);
        // Helpers
        if (line != null) {
            context2d.setLineWidth(1.0);
            context2d.setStrokeStyle(CssColor.make(0, 0, 255));
            context2d.beginPath();
            context2d.moveTo(line.getPoint1().getX(), line.getPoint1().getY());
            context2d.lineTo(line.getPoint2().getX(), line.getPoint2().getY());
            context2d.stroke();
        }
        // Item
        double angel = syncBaseItem.getSyncItemArea().getAngel();
        int imageNr = syncBaseItem.getSyncItemArea().getBoundingBox().angelToImageNr(angel);
        Index position = syncBaseItem.getSyncItemArea().getTopLeftFromImagePosition();
        context2d.drawImage(imageLoader.getImage(imageNr), position.getX(), position.getY());
        // Bounding box
        context2d.setLineWidth(2);
        boundingBoxControl.draw(syncItem.getSyncItemArea(), context2d);
    }

    private List<Index> correctPathAngel(List<Index> pathToDestination, BoundingBox boundingBox) {
        Index start = pathToDestination.get(0);
        Index end = pathToDestination.get(1);
        double distance = start.getDistance(end);
        double angel = start.getAngleToNord(end);
        double allowedAngel = boundingBox.getAllowedAngel(angel);
        Index newEnd = start.getPointFromAngelToNord(allowedAngel, distance);
        pathToDestination.set(1, newEnd);
        line = new Line(start, newEnd);
        return pathToDestination;
    }

    /**
     * @param currentImage 1..x
     */
    public void setImageNumber(int currentImage) {
        double allowedAngel = syncItem.getSyncItemArea().getBoundingBox().imageNumberToAngel(currentImage - 1);
        syncItem.getSyncItemArea().setAngel(allowedAngel);
    }
}
