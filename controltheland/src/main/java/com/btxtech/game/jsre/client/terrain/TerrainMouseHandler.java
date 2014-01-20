package com.btxtech.game.jsre.client.terrain;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.Game;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.StartPointMode;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.cockpit.CockpitMode;
import com.btxtech.game.jsre.client.cockpit.CursorHandler;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.cockpit.GroupSelectionFrame;
import com.btxtech.game.jsre.client.cockpit.ItemMouseOverHandler;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.cockpit.SideCockpit;
import com.btxtech.game.jsre.client.cockpit.chat.ChatCockpit;
import com.btxtech.game.jsre.client.cockpit.item.ToBeBuildPlacer;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryItemPlacer;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBoxItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.btxtech.game.jsre.common.perfmon.Perfmon;
import com.btxtech.game.jsre.common.perfmon.PerfmonEnum;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;

/**
 * User: beat
 * Date: 29.07.12
 * Time: 18:53
 */
public class TerrainMouseHandler implements MouseMoveHandler {
    private Canvas canvas;
    private TerrainView terrainView;
    private TerrainScrollHandler terrainScrollHandler;

    public TerrainMouseHandler(final Canvas canvas, final TerrainView terrainView, final TerrainScrollHandler terrainScrollHandler) {
        this.canvas = canvas;
        this.terrainView = terrainView;
        this.terrainScrollHandler = terrainScrollHandler;
        canvas.addMouseMoveHandler(this);
        canvas.addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent mouseDownEvent) {
                TerrainView.getInstance().setFocus();
                try {
                    Perfmon.getInstance().onEntered(PerfmonEnum.TERRAIN_MOUSE_DOWN);
                    ChatCockpit.getInstance().blurFocus();
                    Index relative = GwtCommon.createSaveIndexRelative(mouseDownEvent, canvas.getElement());
                    int absoluteX = relative.getX() + terrainView.getViewOriginLeft();
                    int absoluteY = relative.getY() + terrainView.getViewOriginTop();

                    if (StartPointMode.getInstance().isActive()) {
                        StartPointMode.getInstance().execute(relative.getX(), relative.getY(), absoluteX, absoluteY);
                        return;
                    }

                    if (CockpitMode.getInstance().getMode() == CockpitMode.Mode.LAUNCH) {
                        // TODO ActionHandler.getInstance().executeLaunchCommand(absoluteX, absoluteY);
                        CockpitMode.getInstance().setMode(null);
                        return;
                    }

                    if (CockpitMode.getInstance().getMode() == CockpitMode.Mode.UNLOAD) {
                        if (mouseDownEvent.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
                            CockpitMode.getInstance().setMode(null);
                            SelectionHandler.getInstance().clearSelection();
                        }
                        return;
                    }

                    if (CockpitMode.getInstance().hasGroupSelectionFrame()) {
                        finalizeSelectionFrame(absoluteX, absoluteY);
                        return;
                    }

                    if (CockpitMode.getInstance().hasInventoryItemPlacer()) {
                        finalizeInventoryItemPlacer(absoluteX, absoluteY);
                        return;
                    }

                    if (CockpitMode.getInstance().hasToBeBuildPlacer()) {
                        // Only mouse down. Otherwise a move will be triggered on mouse up
                        return;
                    }

                    SyncItem syncItem = ItemContainer.getInstance().getItemAtAbsolutePosition(new Index(absoluteX, absoluteY));
                    if (syncItem != null) {
                        // On item clicked
                        if (CockpitMode.getInstance().getMode() == CockpitMode.Mode.SELL) {
                            if (syncItem instanceof SyncBaseItem && ClientBase.getInstance().isMyOwnProperty((SyncBaseItem) syncItem)) {
                                CockpitMode.getInstance().setMode(null);
                                Connection.getInstance().sellItem((SyncBaseItem) syncItem);
                            }
                        } else {
                            if (syncItem instanceof SyncResourceItem) {
                                SelectionHandler.getInstance().setTargetSelected(syncItem, mouseDownEvent);
                            } else if (syncItem instanceof SyncBaseItem) {
                                SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
                                if (ClientBase.getInstance().isMyOwnProperty(syncBaseItem)) {
                                    Group group = new Group();
                                    group.addItem(syncBaseItem);
                                    SelectionHandler.getInstance().setItemGroupSelected(group);
                                } else {
                                    SelectionHandler.getInstance().setTargetSelected(syncBaseItem, mouseDownEvent);
                                }
                            } else if (syncItem instanceof SyncBoxItem) {
                                SelectionHandler.getInstance().setTargetSelected(syncItem, mouseDownEvent);
                            } else {
                                throw new IllegalArgumentException(this + " onMouseDown: SyncItem not supported: " + syncItem);
                            }
                        }
                    } else {
                        // On terrain clicked
                        if (CockpitMode.getInstance().getMode() == CockpitMode.Mode.SELL) {
                            return;
                        }
                        if (mouseDownEvent.getNativeButton() == NativeEvent.BUTTON_LEFT) {
                            CockpitMode.getInstance().setGroupSelectionFrame(new GroupSelectionFrame(absoluteX, absoluteY));
                        } else if (mouseDownEvent.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
                            SelectionHandler.getInstance().clearSelection();
                        }
                    }
                    GwtCommon.preventDefault(mouseDownEvent);
                } finally {
                    Perfmon.getInstance().onLeft(PerfmonEnum.TERRAIN_MOUSE_DOWN);
                }
            }
        });
        canvas.addMouseUpHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                try {
                    Perfmon.getInstance().onEntered(PerfmonEnum.TERRAIN_MOUSE_UP);
                    Index relative = GwtCommon.createSaveIndexRelative(event, canvas.getElement());
                    int absoluteX = relative.getX() + terrainView.getViewOriginLeft();
                    int absoluteY = relative.getY() + terrainView.getViewOriginTop();
                    if (event.getNativeButton() == NativeEvent.BUTTON_LEFT) {
                        if (CockpitMode.getInstance().getMode() == CockpitMode.Mode.LAUNCH) {
                            // TODO ActionHandler.getInstance().executeLaunchCommand(absoluteX, absoluteY);
                            CockpitMode.getInstance().setMode(null);
                            return;
                        }
                        if (CockpitMode.getInstance().hasInventoryItemPlacer()) {
                            finalizeInventoryItemPlacer(absoluteX, absoluteY);
                            return;
                        }
                        SyncItem syncItem = ItemContainer.getInstance().getItemAtAbsolutePosition(new Index(absoluteX, absoluteY));
                        if (syncItem != null) {
                            if (CockpitMode.getInstance().hasGroupSelectionFrame()) {
                                finalizeSelectionFrame(absoluteX, absoluteY);
                            }
                        } else {
                            if (CockpitMode.getInstance().getMode() == CockpitMode.Mode.UNLOAD) {
                                executeUnloadContainerCommand(absoluteX, absoluteY);
                            } else if (CockpitMode.getInstance().hasGroupSelectionFrame()) {
                                if (!finalizeSelectionFrame(absoluteX, absoluteY)) {
                                    executeMoveCommand(absoluteX, absoluteY);
                                }
                            } else {
                                executeMoveCommand(absoluteX, absoluteY);
                            }
                        }
                    }
                } finally {
                    Perfmon.getInstance().onLeft(PerfmonEnum.TERRAIN_MOUSE_UP);
                }
            }
        });
        canvas.addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                terrainScrollHandler.executeAutoScrollMouse(TerrainScrollHandler.ScrollDirection.STOP, TerrainScrollHandler.ScrollDirection.STOP);
            }
        });
    }

    public void onOverlayMouseUp(MouseUpEvent event) {
        try {
            Perfmon.getInstance().onEntered(PerfmonEnum.TERRAIN_MOUSE_UP);

            if (event.getNativeButton() == NativeEvent.BUTTON_LEFT) {
                if (CockpitMode.getInstance().hasToBeBuildPlacer()) {
                    Index relative = GwtCommon.createSaveIndexRelative(event, canvas.getElement());
                    int absoluteX = relative.getX() + terrainView.getViewOriginLeft();
                    int absoluteY = relative.getY() + terrainView.getViewOriginTop();
                    finalizeToBeBuildPlacer(absoluteX, absoluteY);
                    return;
                }
            } else if (event.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
                CockpitMode.getInstance().setToBeBuildPlacer(null);
                SelectionHandler.getInstance().clearSelection();
            }
            GwtCommon.preventDefault(event);
        } finally {
            Perfmon.getInstance().onLeft(PerfmonEnum.TERRAIN_MOUSE_UP);
        }
    }

    public void onOverlayMouseDown(MouseDownEvent event) {
        try {
            Perfmon.getInstance().onEntered(PerfmonEnum.TERRAIN_MOUSE_DOWN);
            if (event.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
                CockpitMode.getInstance().setToBeBuildPlacer(null);
                SelectionHandler.getInstance().clearSelection();
            }
            GwtCommon.preventDefault(event);
        } finally {
            Perfmon.getInstance().onLeft(PerfmonEnum.TERRAIN_MOUSE_DOWN);
        }
    }

    public void onOverlayMouseMove(MouseMoveEvent event) {
        try {
            Perfmon.getInstance().onEntered(PerfmonEnum.TERRAIN_MOUSE_MOVE);
            Index relative = GwtCommon.createSaveIndexRelative(event, canvas.getElement());
            int width = canvas.getOffsetWidth();
            int height = canvas.getOffsetHeight();

            terrainScrollHandler.handleMouseMoveScroll(relative.getX(), relative.getY(), width, height);

            int absoluteX = relative.getX() + terrainView.getViewOriginLeft();
            int absoluteY = relative.getY() + terrainView.getViewOriginTop();

            if (CockpitMode.getInstance().hasToBeBuildPlacer()) {
                CockpitMode.getInstance().getToBeBuildPlacer().onMove(relative.getX(), relative.getY(), absoluteX, absoluteY);
                CursorHandler.getInstance().noCursor();
            }
        } finally {
            Perfmon.getInstance().onLeft(PerfmonEnum.TERRAIN_MOUSE_MOVE);
        }
    }

    private void finalizeInventoryItemPlacer(int absoluteX, int absoluteY) {
        InventoryItemPlacer inventoryItemPlacer = CockpitMode.getInstance().getInventoryItemPlacer();
        if (inventoryItemPlacer.execute(absoluteX, absoluteY)) {
            CockpitMode.getInstance().setInventoryItemPlacer(null);
        }
    }

    private boolean finalizeSelectionFrame(int absoluteX, int absoluteY) {
        GroupSelectionFrame groupSelectionFrame = CockpitMode.getInstance().getGroupSelectionFrame();
        CockpitMode.getInstance().setGroupSelectionFrame(null);
        return groupSelectionFrame.execute(absoluteX, absoluteY);
    }

    private void finalizeToBeBuildPlacer(int absoluteX, int absoluteY) {
        ToBeBuildPlacer toBeBuildPlacer = CockpitMode.getInstance().getToBeBuildPlacer();
        if (toBeBuildPlacer.execute(absoluteX, absoluteY)) {
            CockpitMode.getInstance().setToBeBuildPlacer(null);
        }
    }

    private void executeUnloadContainerCommand(int absoluteX, int absoluteY) {
        CockpitMode.getInstance().setMode(null);
        Group selection = SelectionHandler.getInstance().getOwnSelection();
        if (selection == null) {
            return;
        }

        if (selection.getCount() != 1) {
            return;
        }

        SyncBaseItem syncBaseItem = selection.getFirst();
        ActionHandler.getInstance().unloadContainerFindPosition(syncBaseItem, new Index(absoluteX, absoluteY));
    }

    private void executeMoveCommand(int absoluteX, int absoluteY) {
        Group selection = SelectionHandler.getInstance().getOwnSelection();
        if (selection == null) {
            return;
        }

        if (!selection.canMove()) {
            return;
        }

        ActionHandler.getInstance().move(selection.getItems(), new Index(absoluteX, absoluteY));
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        try {
            Perfmon.getInstance().onEntered(PerfmonEnum.TERRAIN_MOUSE_MOVE);
            Index relative = GwtCommon.createSaveIndexRelative(event, canvas.getElement());

            int width = canvas.getOffsetWidth();
            int height = canvas.getOffsetHeight();

            terrainScrollHandler.handleMouseMoveScroll(relative.getX(), relative.getY(), width, height);

            int absoluteX = relative.getX() + terrainView.getViewOriginLeft();
            int absoluteY = relative.getY() + terrainView.getViewOriginTop();

            ItemMouseOverHandler.getInstance().setMouseOver(null);
            if (StartPointMode.getInstance().isActive()) {
                StartPointMode.getInstance().getStartPointPlacer().onMove(relative.getX(), relative.getY(), absoluteX, absoluteY);
            } else if (CockpitMode.getInstance().hasGroupSelectionFrame()) {
                CockpitMode.getInstance().getGroupSelectionFrame().onMove(absoluteX, absoluteY);
            } else if (CockpitMode.getInstance().hasInventoryItemPlacer()) {
                CockpitMode.getInstance().getInventoryItemPlacer().onMove(relative.getX(), relative.getY(), absoluteX, absoluteY);
            } else if (CockpitMode.getInstance().hasToBeBuildPlacer()) {
                CockpitMode.getInstance().getToBeBuildPlacer().onMove(relative.getX(), relative.getY(), absoluteX, absoluteY);
                CursorHandler.getInstance().noCursor();
            } else {
                SyncItem syncItem = ItemContainer.getInstance().getItemAtAbsolutePosition(new Index(absoluteX, absoluteY));
                ItemMouseOverHandler.getInstance().setMouseOver(syncItem);
                CursorHandler.getInstance().handleMouseMove(syncItem, absoluteX, absoluteY);
            }

            if (Game.isDebug()) {
                SideCockpit.getInstance().debugAbsoluteCursorPos(relative.getX() + terrainView.getViewOriginLeft(), relative.getY() + terrainView.getViewOriginTop());
            }
        } finally {
            Perfmon.getInstance().onLeft(PerfmonEnum.TERRAIN_MOUSE_MOVE);
        }
    }
}
