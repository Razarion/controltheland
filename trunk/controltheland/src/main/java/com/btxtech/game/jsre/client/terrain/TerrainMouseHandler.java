package com.btxtech.game.jsre.client.terrain;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.Game;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.cockpit.ChatCockpit;
import com.btxtech.game.jsre.client.cockpit.CockpitMode;
import com.btxtech.game.jsre.client.cockpit.CursorHandler;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.cockpit.GroupSelectionFrame;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.cockpit.SideCockpit;
import com.btxtech.game.jsre.client.cockpit.item.ToBeBuildPlacer;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryItemPlacer;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.territory.ClientTerritoryService;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBoxItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.btxtech.game.jsre.common.perfmon.Perfmon;
import com.btxtech.game.jsre.common.perfmon.PerfmonEnum;
import com.btxtech.game.jsre.common.perfmon.TimerPerfmon;
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
import com.google.gwt.user.client.Timer;

/**
 * User: beat
 * Date: 29.07.12
 * Time: 18:53
 */
public class TerrainMouseHandler implements MouseMoveHandler {
    public enum ScrollDirection {
        NORTH,
        SOUTH,
        WEST,
        EAST,
        STOP
    }

    private static final int SCROLL_AUTO_MOUSE_DETECTION_WIDTH = 40;
    private static final int SCROLL_TIMER_DELAY = 40;
    private static final int SCROLL_AUTO_DISTANCE = 60;
    private ScrollDirection scrollDirectionXKey = ScrollDirection.STOP;
    private ScrollDirection scrollDirectionYKey = ScrollDirection.STOP;
    private ScrollDirection scrollDirectionXMouse = ScrollDirection.STOP;
    private ScrollDirection scrollDirectionYMouse = ScrollDirection.STOP;
    private ScrollDirection scrollDirectionX = ScrollDirection.STOP;
    private ScrollDirection scrollDirectionY = ScrollDirection.STOP;
    private Canvas canvas;
    private TerrainView terrainView;
    private Timer timer = new TimerPerfmon(PerfmonEnum.SCROLL) {
        @Override
        public void runPerfmon() {
            autoScroll();
        }
    };

    public TerrainMouseHandler(final Canvas canvas, final TerrainView terrainView) {
        this.canvas = canvas;
        this.terrainView = terrainView;
        canvas.addMouseMoveHandler(this);
        canvas.addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent mouseDownEvent) {
                try {
                    Perfmon.getInstance().onEntered(PerfmonEnum.TERRAIN_MOUSE_DOWN);
                    ChatCockpit.getInstance().blurFocus();
                    int absoluteX = mouseDownEvent.getRelativeX(canvas.getElement()) + terrainView.getViewOriginLeft();
                    int absoluteY = mouseDownEvent.getRelativeY(canvas.getElement()) + terrainView.getViewOriginTop();

                    if (CockpitMode.getInstance().getMode() == CockpitMode.Mode.LAUNCH) {
                        ActionHandler.getInstance().executeLaunchCommand(absoluteX, absoluteY);
                        CockpitMode.getInstance().setMode(null);
                        return;
                    }

                    if (CockpitMode.getInstance().getMode() == CockpitMode.Mode.UNLOAD) {
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
                                Connection.getInstance().sellItem((SyncBaseItem) syncItem);
                                CockpitMode.getInstance().setMode(null);
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
                    int absoluteX = event.getRelativeX(canvas.getElement()) + terrainView.getViewOriginLeft();
                    int absoluteY = event.getRelativeY(canvas.getElement()) + terrainView.getViewOriginTop();
                    if (event.getNativeButton() == NativeEvent.BUTTON_LEFT) {
                        if (CockpitMode.getInstance().getMode() == CockpitMode.Mode.LAUNCH) {
                            ActionHandler.getInstance().executeLaunchCommand(absoluteX, absoluteY);
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
                executeAutoScrollMouse(TerrainMouseHandler.ScrollDirection.STOP, TerrainMouseHandler.ScrollDirection.STOP);
            }
        });
    }

    public void onOverlayMouseUp(MouseUpEvent event) {
        try {
            Perfmon.getInstance().onEntered(PerfmonEnum.TERRAIN_MOUSE_UP);

            if (event.getNativeButton() == NativeEvent.BUTTON_LEFT) {
                if (CockpitMode.getInstance().hasToBeBuildPlacer()) {
                    int absoluteX = event.getRelativeX(canvas.getElement()) + terrainView.getViewOriginLeft();
                    int absoluteY = event.getRelativeY(canvas.getElement()) + terrainView.getViewOriginTop();
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
            int relativeX = event.getRelativeX(canvas.getElement());
            int relativeY = event.getRelativeY(canvas.getElement());
            int height = canvas.getOffsetHeight();
            int width = canvas.getOffsetWidth();

            handleMouseMoveScroll(relativeX, relativeY, height, width);

            int absoluteX = event.getRelativeX(canvas.getElement()) + terrainView.getViewOriginLeft();
            int absoluteY = event.getRelativeY(canvas.getElement()) + terrainView.getViewOriginTop();

            if (CockpitMode.getInstance().hasToBeBuildPlacer()) {
                CockpitMode.getInstance().getToBeBuildPlacer().onMove(relativeX, relativeY, absoluteX, absoluteY);
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
        Index position = new Index(absoluteX, absoluteY);
        if (!ClientTerritoryService.getInstance().isAllowed(position, syncBaseItem)) {
            return;
        }

        ActionHandler.getInstance().unloadContainerFindPosition(syncBaseItem, position);
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
            int relativeX = event.getRelativeX(canvas.getElement());
            int relativeY = event.getRelativeY(canvas.getElement());
            int height = canvas.getOffsetHeight();
            int width = canvas.getOffsetWidth();

            handleMouseMoveScroll(relativeX, relativeY, height, width);

            int absoluteX = event.getRelativeX(canvas.getElement()) + terrainView.getViewOriginLeft();
            int absoluteY = event.getRelativeY(canvas.getElement()) + terrainView.getViewOriginTop();

            if (CockpitMode.getInstance().hasGroupSelectionFrame()) {
                CockpitMode.getInstance().getGroupSelectionFrame().onMove(absoluteX, absoluteY);
            } else if (CockpitMode.getInstance().hasInventoryItemPlacer()) {
                CockpitMode.getInstance().getInventoryItemPlacer().onMove(relativeX, relativeY, absoluteX, absoluteY);
            } else if (CockpitMode.getInstance().hasToBeBuildPlacer()) {
                CockpitMode.getInstance().getToBeBuildPlacer().onMove(relativeX, relativeY, absoluteX, absoluteY);
                CursorHandler.getInstance().noCursor();
            } else {
                SyncItem syncItem = ItemContainer.getInstance().getItemAtAbsolutePosition(new Index(absoluteX, absoluteY));
                CursorHandler.getInstance().handleMouseMove(syncItem, absoluteX, absoluteY);
            }

            if (Game.isDebug()) {
                SideCockpit.getInstance().debugAbsoluteCursorPos(relativeX + terrainView.getViewOriginLeft(), relativeY + terrainView.getViewOriginTop());
            }
        } finally {
            Perfmon.getInstance().onLeft(PerfmonEnum.TERRAIN_MOUSE_MOVE);
        }
    }

    private void handleMouseMoveScroll(int x, int y, int height, int width) {
        ScrollDirection tmpScrollDirectionX = ScrollDirection.STOP;
        ScrollDirection tmpScrollDirectionY = ScrollDirection.STOP;
        if (x < SCROLL_AUTO_MOUSE_DETECTION_WIDTH) {
            tmpScrollDirectionX = ScrollDirection.WEST;
        } else if (x > width - SCROLL_AUTO_MOUSE_DETECTION_WIDTH) {
            tmpScrollDirectionX = ScrollDirection.EAST;
        }

        if (y < SCROLL_AUTO_MOUSE_DETECTION_WIDTH) {
            tmpScrollDirectionY = ScrollDirection.NORTH;
        } else if (y > height - SCROLL_AUTO_MOUSE_DETECTION_WIDTH) {
            tmpScrollDirectionY = ScrollDirection.SOUTH;
        }
        executeAutoScrollMouse(tmpScrollDirectionX, tmpScrollDirectionY);
    }

    public void executeAutoScrollKey(ScrollDirection tmpScrollDirectionX, ScrollDirection tmpScrollDirectionY) {
        if (tmpScrollDirectionX != scrollDirectionXKey || tmpScrollDirectionY != scrollDirectionYKey) {
            if (tmpScrollDirectionX != null) {
                scrollDirectionXKey = tmpScrollDirectionX;
            }
            if (tmpScrollDirectionY != null) {
                scrollDirectionYKey = tmpScrollDirectionY;
            }
            executeAutoScroll();
        }
    }

    private void executeAutoScrollMouse(ScrollDirection tmpScrollDirectionX, ScrollDirection tmpScrollDirectionY) {
        if (tmpScrollDirectionX != scrollDirectionXMouse || tmpScrollDirectionY != scrollDirectionYMouse) {
            scrollDirectionXMouse = tmpScrollDirectionX;
            scrollDirectionYMouse = tmpScrollDirectionY;
            executeAutoScroll();
        }
    }

    private void executeAutoScroll() {
        ScrollDirection newScrollDirectionX = ScrollDirection.STOP;
        if (scrollDirectionXKey != ScrollDirection.STOP) {
            newScrollDirectionX = scrollDirectionXKey;
        } else if (scrollDirectionXMouse != ScrollDirection.STOP) {
            newScrollDirectionX = scrollDirectionXMouse;
        }

        ScrollDirection newScrollDirectionY = ScrollDirection.STOP;
        if (scrollDirectionYKey != ScrollDirection.STOP) {
            newScrollDirectionY = scrollDirectionYKey;
        } else if (scrollDirectionYMouse != ScrollDirection.STOP) {
            newScrollDirectionY = scrollDirectionYMouse;
        }

        if (newScrollDirectionX != scrollDirectionX || newScrollDirectionY != scrollDirectionY) {
            boolean isTimerRunningOld = scrollDirectionX != ScrollDirection.STOP || scrollDirectionY != ScrollDirection.STOP;
            boolean isTimerRunningNew = newScrollDirectionX != ScrollDirection.STOP || newScrollDirectionY != ScrollDirection.STOP;
            scrollDirectionX = newScrollDirectionX;
            scrollDirectionY = newScrollDirectionY;
            if (isTimerRunningOld != isTimerRunningNew) {
                if (isTimerRunningNew) {
                    autoScroll();
                    timer.scheduleRepeating(SCROLL_TIMER_DELAY);
                } else {
                    timer.cancel();
                }
            }
        }
    }

    private void autoScroll() {
        int scrollX = 0;
        if (scrollDirectionX == ScrollDirection.WEST) {
            scrollX = -SCROLL_AUTO_DISTANCE;
        } else if (scrollDirectionX == ScrollDirection.EAST) {
            scrollX = SCROLL_AUTO_DISTANCE;
        }

        int scrollY = 0;
        if (scrollDirectionY == ScrollDirection.SOUTH) {
            scrollY = SCROLL_AUTO_DISTANCE;
        } else if (scrollDirectionY == ScrollDirection.NORTH) {
            scrollY = -SCROLL_AUTO_DISTANCE;
        }

        terrainView.moveDelta(scrollX, scrollY);
    }
}
