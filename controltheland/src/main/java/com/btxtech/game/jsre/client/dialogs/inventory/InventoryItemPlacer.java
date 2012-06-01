package com.btxtech.game.jsre.client.dialogs.inventory;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.territory.ClientTerritoryService;
import com.btxtech.game.jsre.common.Html5NotSupportedException;
import com.btxtech.game.jsre.common.ImageLoader;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 29.05.12
 * Time: 18:02
 */
public class InventoryItemPlacer {
    private static InventoryItemPlacer INSTANCE;
    private Canvas canvas;
    private HandlerRegistration handlerRegistration;
    private Index relativeMiddlePos = new Index(200, 200);
    private Collection<Index> normalizedPositionsToPlace;
    private Collection<Index> relativePositionsToPlace;
    private int itemFreeRadius;
    private BaseItemType baseItemType;
    private ImageElement imageElement;
    private boolean isTerrainOk;
    private boolean isTerritoryOk;
    private boolean isItemsOk;
    private boolean isEnemiesOk;
    private Logger log = Logger.getLogger(InventoryItemPlacer.class.getName());

    public static void show(InventoryItemInfo inventoryItemInfo) {
        if (INSTANCE == null) {
            INSTANCE = new InventoryItemPlacer(inventoryItemInfo.getInventoryItemId(),
                    inventoryItemInfo.getBaseItemTypeId(),
                    inventoryItemInfo.getItemCount(),
                    inventoryItemInfo.getItemFreeRange());
        }
    }

    private InventoryItemPlacer(final int inventoryItemId, int baseItemTypeId, int itemCount, int itemFreeRange) {
        canvas = Canvas.createIfSupported();
        if (canvas == null) {
            throw new Html5NotSupportedException("InventoryItemPlacer");
        }
        canvas.setWidth("100%");
        canvas.setHeight("100%");
        canvas.getElement().getStyle().setZIndex(Constants.Z_INDEX_INVENTORY_ITEM_PLACER);
        MapWindow.getAbsolutePanel().add(canvas, 0, 0);
        canvas.setCoordinateSpaceWidth(MapWindow.getAbsolutePanel().getOffsetWidth());
        canvas.setCoordinateSpaceHeight(MapWindow.getAbsolutePanel().getOffsetHeight());
        handlerRegistration = Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent resizeEvent) {
                Context2d context2d = canvas.getContext2d();
                context2d.clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
                canvas.setCoordinateSpaceWidth(canvas.getOffsetWidth());
                canvas.setCoordinateSpaceHeight(canvas.getOffsetHeight());
                draw();
            }
        });
        canvas.addMouseMoveHandler(new MouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event) {
                relativeMiddlePos = new Index(event.getRelativeX(canvas.getElement()), event.getRelativeY(canvas.getElement()));
                Index absolutePos = TerrainView.getInstance().toAbsoluteIndex(relativeMiddlePos);
                relativePositionsToPlace = Index.add(normalizedPositionsToPlace, relativeMiddlePos);
                for (Index normalizedPosition : normalizedPositionsToPlace) {
                    if (!checkPlacingAllowed(normalizedPosition.add(absolutePos))) {
                        break;
                    }
                }
                draw();
            }
        });
        try {
            baseItemType = (BaseItemType) ItemContainer.getInstance().getItemType(baseItemTypeId);
        } catch (NoSuchItemTypeException e) {
            log.log(Level.SEVERE, "InventoryItemPlacer() ", e);
        }
        calculateNormalizedPlacePositions(itemCount, itemFreeRange);
        canvas.setFocus(true);
        canvas.addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
                    close();
                }
            }
        });
        canvas.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                close();
            }
        });
        canvas.addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                if (isTerrainOk && isTerritoryOk && isItemsOk && isEnemiesOk) {
                    Index absolutePos = TerrainView.getInstance().toAbsoluteIndex(relativeMiddlePos);
                    Connection.getInstance().useInventoryItem(inventoryItemId, Index.add(normalizedPositionsToPlace, absolutePos));
                    close();
                }
            }
        });
        ImageLoader imageLoader = new ImageLoader();
        imageLoader.addImageUrl(ImageHandler.getItemTypeSpriteMapUrl(baseItemType.getId()));
        imageLoader.startLoading(new ImageLoader.Listener() {
            @Override
            public void onLoaded(ImageElement[] imageElements) {
                imageElement = imageElements[0];
                draw();
            }
        });
        draw();
    }

    void calculateNormalizedPlacePositions(int itemCount, int itemFreeRange) {
        normalizedPositionsToPlace = new ArrayList<Index>();
        double value = Math.sqrt(itemCount);
        int columns = (int) Math.ceil(value);
        int offsetX = (columns - 1) * baseItemType.getBoundingBox().getImageWidth() / 2;
        int rows = (int) Math.round(value);
        int offsetY = (rows - 1) * baseItemType.getBoundingBox().getImageHeight() / 2;
        int count = 0;
        for (int x = 0; x < columns; x++) {
            for (int y = 0; y < rows; y++) {
                int xPos = x * baseItemType.getBoundingBox().getImageWidth() - offsetX;
                int yPos = y * baseItemType.getBoundingBox().getImageHeight() - offsetY;
                count++;
                if (count <= itemCount) {
                    normalizedPositionsToPlace.add(new Index(xPos, yPos));
                }
            }
        }
        itemFreeRadius = itemFreeRange + (int) MathHelper.getPythagoras(columns * baseItemType.getBoundingBox().getImageWidth(), rows * baseItemType.getBoundingBox().getImageHeight()) / 2;
    }

    private boolean checkPlacingAllowed(Index absolutePos) {
        isTerrainOk = false;
        isTerritoryOk = false;
        isItemsOk = false;
        isEnemiesOk = false;

        isTerrainOk = TerrainView.getInstance().getTerrainHandler().isFree(absolutePos, baseItemType);
        if (!isTerrainOk) {
            return false;
        }
        isTerritoryOk = ClientTerritoryService.getInstance().isAllowed(absolutePos, baseItemType);
        if (!isTerritoryOk) {
            return false;
        }
        Rectangle itemRect = baseItemType.getBoundingBox().getRectangle(absolutePos);
        isItemsOk = !ItemContainer.getInstance().hasItemsInRectangle(itemRect);
        if (!isItemsOk) {
            return false;
        }
        isEnemiesOk = !ItemContainer.getInstance().hasEnemyInRange(ClientBase.getInstance().getSimpleBase(), absolutePos, itemFreeRadius);
        return isEnemiesOk;
    }

    public void close() {
        handlerRegistration.removeHandler();
        MapWindow.getAbsolutePanel().remove(canvas);
        INSTANCE = null;
    }

    private void draw() {
        Context2d context2d = canvas.getContext2d();
        context2d.clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
        context2d.setGlobalAlpha(0.5);

        // Draw circle
        context2d.beginPath();
        context2d.arc(relativeMiddlePos.getX(), relativeMiddlePos.getY(), itemFreeRadius, 0, 2 * Math.PI, false);
        if (isTerrainOk && isTerritoryOk && isItemsOk && isEnemiesOk) {
            context2d.setFillStyle("rgb(0, 200, 0)");
            context2d.fill();
        } else {
            context2d.setFillStyle("rgb(200, 0, 0)");
            context2d.fill();
        }
        context2d.setLineWidth(2);
        context2d.setStrokeStyle("black");
        context2d.stroke();

        // Draw Items
        if (imageElement != null && relativePositionsToPlace != null) {
            for (Index index : relativePositionsToPlace) {
                context2d.drawImage(imageElement,
                        baseItemType.getBoundingBox().angelToImageOffset(baseItemType.getBoundingBox().getCosmeticAngel()),
                        0,
                        baseItemType.getBoundingBox().getImageWidth(),
                        baseItemType.getBoundingBox().getImageHeight(),
                        index.getX() - baseItemType.getBoundingBox().getImageWidth() / 2,
                        index.getY() - baseItemType.getBoundingBox().getImageHeight() / 2,
                        baseItemType.getBoundingBox().getImageWidth(),
                        baseItemType.getBoundingBox().getImageHeight());
            }
        }

        // Draw text
        if (!isTerrainOk || !isTerritoryOk || !isItemsOk || !isEnemiesOk) {
            context2d.setGlobalAlpha(1.0);
            context2d.setFont("20px Arial");
            context2d.setTextAlign(Context2d.TextAlign.CENTER);
            context2d.setShadowColor("#000000");
            context2d.setShadowOffsetX(2);
            context2d.setShadowOffsetY(2);
            context2d.setShadowBlur(2);
            context2d.setFillStyle("#FFFFFF");
            if (!isTerrainOk) {
                context2d.fillText("You can not place here", relativeMiddlePos.getX(), relativeMiddlePos.getY());
            } else if (!isTerritoryOk) {
                context2d.fillText("Items not allowed on territory", relativeMiddlePos.getX(), relativeMiddlePos.getY());
            } else if (!isItemsOk) {
                context2d.fillText("Not allowed to place on other items", relativeMiddlePos.getX(), relativeMiddlePos.getY());
            } else if (!isEnemiesOk) {
                context2d.fillText("Enemy items are too near", relativeMiddlePos.getX(), relativeMiddlePos.getY());
            }
            context2d.setGlobalAlpha(0.5);
            context2d.setShadowColor(null);
            context2d.setShadowOffsetX(0);
            context2d.setShadowOffsetY(0);
            context2d.setShadowBlur(0);
        }
    }
}
