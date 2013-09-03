package com.btxtech.game.jsre.client.simulation;

import com.btxtech.game.jsre.client.common.AbstractGwtTest;
import com.btxtech.game.jsre.client.item.ItemContainer;
import org.junit.Ignore;

/**
 * User: beat
 * Date: 15.03.2012
 * Time: 23:43:29
 */
// Does not work with maven
@Ignore
public class TestTransporter extends AbstractGwtTest {

    @Override
    public String getModuleName() {
        return "com.btxtech.game.jsre.GameTestTransporter";
    }

    public void testUnloadCursorNoItems() throws Exception {
        startColdSimulated(new GwtTestRunnable() {
            @Override
            public void run() throws Exception {
 /*               assertEquals(11, ItemContainer.getInstance().getItems().size());

                assertCursor("default", MapWindow.getAbsolutePanel());
                // Select unit and move cursor around
               // ClientSyncItemView transporter = getFirstClientSyncItemView(ITEM_CONTAINER);
               // transporter.onMouseDown(new TestMouseDownEvent());
                MapWindow.getInstance().onMouseMove(new TestMouseMoveEvent(100, 100));
                assertCursor("url(/images/cursors/nogo.cur), pointer", MapWindow.getAbsolutePanel());
                MapWindow.getInstance().onMouseMove(new TestMouseMoveEvent(1450, 100));
                assertCursor("url(/images/cursors/go.cur), crosshair", MapWindow.getAbsolutePanel());
                MapWindow.getInstance().onMouseMove(new TestMouseMoveEvent(1550, 100));
                assertCursor("url(/images/cursors/go.cur), crosshair", MapWindow.getAbsolutePanel());
                MapWindow.getInstance().onMouseMove(new TestMouseMoveEvent(1650, 100));
                assertCursor("url(/images/cursors/go.cur), crosshair", MapWindow.getAbsolutePanel());
                // Assert & click unload button
                CustomButton customButton = (CustomButton) getDebugUIObject("unloadButton");
                assertFalse(customButton.isEnabled());
                customButton.onBrowserEvent(Event.as(new TestMouseDownEvent().getNativeEvent()));
                customButton.onBrowserEvent(Event.as(new TestMouseOverEvent(customButton).getNativeEvent()));
                customButton.onBrowserEvent(Event.as(new TestMouseUpEvent().getNativeEvent()));
                MapWindow.getInstance().onMouseMove(new TestMouseMoveEvent(100, 100));
                assertCursor("url(/images/cursors/nogo.cur), pointer", MapWindow.getAbsolutePanel());
                MapWindow.getInstance().onMouseMove(new TestMouseMoveEvent(1450, 100));
                assertCursor("url(/images/cursors/go.cur), crosshair", MapWindow.getAbsolutePanel());
                MapWindow.getInstance().onMouseMove(new TestMouseMoveEvent(1550, 100));
                assertCursor("url(/images/cursors/go.cur), crosshair", MapWindow.getAbsolutePanel());
                MapWindow.getInstance().onMouseMove(new TestMouseMoveEvent(1650, 100));
                assertCursor("url(/images/cursors/go.cur), crosshair", MapWindow.getAbsolutePanel());
                finishTest();  */
            }
        });
    }

    public void testLoadCursor() throws Exception {
        startColdSimulated(new GwtTestRunnable() {
            @Override
            public void run() throws Exception {
                assertEquals(11, ItemContainer.getInstance().getItems().size());

                // Select movable
               // ClientSyncItemView movable = getFirstClientSyncItemView(ITEM_MOVABLE);
               // movable.onMouseDown(new TestMouseDownEvent());
                // Check transporter out of load range
               // final ClientSyncItemView transporter = getFirstClientSyncItemView(ITEM_CONTAINER);
              //  transporter.onMouseOver(new TestMouseOverEvent(transporter));
              //  assertCursor("url(/images/cursors/noload.cur), pointer", transporter);
                // Move to load range
               // TerrainView.getInstance().onMouseUp(new TestMouseUpEvent(1260, 700, NativeEvent.BUTTON_LEFT));
              /*  executeIfActionServiceIdle(new GwtTestRunnable() {
                   @Override
                    public void run() throws Exception {
                        transporter.onMouseOver(new TestMouseOverEvent(transporter));
                        assertCursor("url(/images/cursors/load.cur), s-resize", transporter);
                        finishTest();
                    }
                });  */
            }
        });
    }

    public void testLoadUnloadCursor1Item() throws Exception {
        startColdSimulated(new GwtTestRunnable() {
            @Override
            public void run() throws Exception {
                assertEquals(11, ItemContainer.getInstance().getItems().size());

                // Select movable
              //  ClientSyncItemView movable = getFirstClientSyncItemView(ITEM_MOVABLE);
              //  movable.onMouseDown(new TestMouseDownEvent());
                // Move to load range
            //    TerrainView.getInstance().onMouseUp(new TestMouseUpEvent(1260, 700, NativeEvent.BUTTON_LEFT));
             /*   executeIfActionServiceIdle(new GwtTestRunnable() {
                    @Override
                    public void run() throws Exception {
                        // Load container
                 //       final ClientSyncItemView transporter = getFirstClientSyncItemView(ITEM_CONTAINER);
                 //       transporter.onMouseDown(new TestMouseDownEvent());

                        executeIfActionServiceIdle(new GwtTestRunnable() {
                            @Override
                            public void run() throws Exception {
                                assertEquals(1, transporter.getClientSyncItem().getSyncBaseItem().getSyncItemContainer().getContainedItems().size());
                                // Test if selection cleared
                                MapWindow.getInstance().onMouseMove(new TestMouseMoveEvent(100, 100));
                                assertCursor("default", MapWindow.getAbsolutePanel());
                                MapWindow.getInstance().onMouseMove(new TestMouseMoveEvent(1450, 100));
                                assertCursor("default", MapWindow.getAbsolutePanel());
                                MapWindow.getInstance().onMouseMove(new TestMouseMoveEvent(1550, 100));
                                assertCursor("default", MapWindow.getAbsolutePanel());
                                MapWindow.getInstance().onMouseMove(new TestMouseMoveEvent(1650, 100));
                                assertCursor("default", MapWindow.getAbsolutePanel());
                                // Click territory -> nothing happens
                                TerrainView.getInstance().onMouseUp(new TestMouseUpEvent(100, 100, NativeEvent.BUTTON_LEFT));
                                assertFalse(ActionHandler.getInstance().isBusy());
                                TerrainView.getInstance().onMouseUp(new TestMouseUpEvent(1450, 100, NativeEvent.BUTTON_LEFT));
                                assertFalse(ActionHandler.getInstance().isBusy());
                                TerrainView.getInstance().onMouseUp(new TestMouseUpEvent(1550, 100, NativeEvent.BUTTON_LEFT));
                                assertFalse(ActionHandler.getInstance().isBusy());
                                TerrainView.getInstance().onMouseUp(new TestMouseUpEvent(1650, 100, NativeEvent.BUTTON_LEFT));
                                assertFalse(ActionHandler.getInstance().isBusy());
                                // Select transporter
                                transporter.onMouseDown(new TestMouseDownEvent());
                                MapWindow.getInstance().onMouseMove(new TestMouseMoveEvent(100, 100));
                                assertCursor("url(/images/cursors/nogo.cur), pointer", MapWindow.getAbsolutePanel());
                                MapWindow.getInstance().onMouseMove(new TestMouseMoveEvent(1450, 100));
                                assertCursor("url(/images/cursors/go.cur), crosshair", MapWindow.getAbsolutePanel());
                                MapWindow.getInstance().onMouseMove(new TestMouseMoveEvent(1550, 100));
                                assertCursor("url(/images/cursors/go.cur), crosshair", MapWindow.getAbsolutePanel());
                                MapWindow.getInstance().onMouseMove(new TestMouseMoveEvent(1650, 100));
                                assertCursor("url(/images/cursors/go.cur), crosshair", MapWindow.getAbsolutePanel());
                                // Assert & click unload button
                                CustomButton customButton = (CustomButton) getDebugUIObject("unloadButton");
                                assertTrue(customButton.isEnabled());
                                // does not work customButton.onBrowserEvent(Event.as(new TestMouseDownEvent().getNativeEvent()));
                                // does not work customButton.onBrowserEvent(Event.as(new TestMouseOverEvent(MapWindow.getAbsolutePanel()).getNativeEvent()));
                                // does not work customButton.onBrowserEvent(Event.as(new TestMouseUpEvent().getNativeEvent()));

                                // Invalid position 1
                                SideCockpit.getInstance().getCockpitMode().setUnloadMode();
                                MapWindow.getInstance().onMouseMove(new TestMouseMoveEvent(100, 100));
                                assertCursor("url(/images/cursors/nounload.cur), pointer", MapWindow.getAbsolutePanel());
                                TerrainView.getInstance().onMouseUp(new TestMouseUpEvent(100, 100, NativeEvent.BUTTON_LEFT));
                                assertFalse(ActionHandler.getInstance().isBusy());
                                assertFalse(ItemCockpit.getInstance().isActive());
                                MapWindow.getInstance().onMouseMove(new TestMouseMoveEvent(100, 100));
                                assertCursor("url(/images/cursors/nogo.cur), pointer", MapWindow.getAbsolutePanel());

                                // Invalid position 2
                                transporter.onMouseDown(new TestMouseDownEvent());
                                customButton = (CustomButton) getDebugUIObject("unloadButton");
                                assertTrue(customButton.isEnabled());
                                SideCockpit.getInstance().getCockpitMode().setUnloadMode();
                                MapWindow.getInstance().onMouseMove(new TestMouseMoveEvent(1450, 700));
                                assertCursor("url(/images/cursors/nounload.cur), pointer", MapWindow.getAbsolutePanel());
                                TerrainView.getInstance().onMouseUp(new TestMouseUpEvent(1450, 700, NativeEvent.BUTTON_LEFT));
                                assertFalse(ActionHandler.getInstance().isBusy());
                                assertFalse(ItemCockpit.getInstance().isActive());
                                MapWindow.getInstance().onMouseMove(new TestMouseMoveEvent(1450, 700));
                                assertCursor("url(/images/cursors/go.cur), crosshair", MapWindow.getAbsolutePanel());

                                // Invalid position 3
                                transporter.onMouseDown(new TestMouseDownEvent());
                                customButton = (CustomButton) getDebugUIObject("unloadButton");
                                assertTrue(customButton.isEnabled());
                                SideCockpit.getInstance().getCockpitMode().setUnloadMode();
                                MapWindow.getInstance().onMouseMove(new TestMouseMoveEvent(1550, 700));
                                assertCursor("url(/images/cursors/nounload.cur), pointer", MapWindow.getAbsolutePanel());
                                TerrainView.getInstance().onMouseUp(new TestMouseUpEvent(1550, 700, NativeEvent.BUTTON_LEFT));
                                assertFalse(ActionHandler.getInstance().isBusy());
                                assertFalse(ItemCockpit.getInstance().isActive());
                                MapWindow.getInstance().onMouseMove(new TestMouseMoveEvent(1550, 700));
                                assertCursor("url(/images/cursors/go.cur), crosshair", MapWindow.getAbsolutePanel());

                                // Invalid position 4
                                transporter.onMouseDown(new TestMouseDownEvent());
                                customButton = (CustomButton) getDebugUIObject("unloadButton");
                                assertTrue(customButton.isEnabled());
                                SideCockpit.getInstance().getCockpitMode().setUnloadMode();
                                MapWindow.getInstance().onMouseMove(new TestMouseMoveEvent(1650, 700));
                                assertCursor("url(/images/cursors/nounload.cur), pointer", MapWindow.getAbsolutePanel());
                                TerrainView.getInstance().onMouseUp(new TestMouseUpEvent(1650, 700, NativeEvent.BUTTON_LEFT));
                                assertFalse(ActionHandler.getInstance().isBusy());
                                assertFalse(ItemCockpit.getInstance().isActive());
                                MapWindow.getInstance().onMouseMove(new TestMouseMoveEvent(1650, 700));
                                assertCursor("url(/images/cursors/go.cur), crosshair", MapWindow.getAbsolutePanel());
                                assertEquals(1, transporter.getClientSyncItem().getSyncBaseItem().getSyncItemContainer().getContainedItems().size());

                                // position 4
                                transporter.onMouseDown(new TestMouseDownEvent());
                                customButton = (CustomButton) getDebugUIObject("unloadButton");
                                assertTrue(customButton.isEnabled());
                                SideCockpit.getInstance().getCockpitMode().setUnloadMode();
                                MapWindow.getInstance().onMouseMove(new TestMouseMoveEvent(1320, 700));
                                assertCursor("url(/images/cursors/unload.cur), n-resize", MapWindow.getAbsolutePanel());
                                TerrainView.getInstance().onMouseUp(new TestMouseUpEvent(1320, 700, NativeEvent.BUTTON_LEFT));
                                assertFalse(ItemCockpit.getInstance().isActive());
                                executeIfActionServiceIdle(new GwtTestRunnable() {
                                    @Override
                                    public void run() throws Exception {
                                        assertEquals(0, transporter.getClientSyncItem().getSyncBaseItem().getSyncItemContainer().getContainedItems().size());
                                        // Test if selection cleared
                                        finishTest();
                                    }
                                });
                            }
                        });
                    }
                }); */
            }
        });
    }

    public void testLoadUnloadCursor1ItemMaxRange() throws Exception {
        startColdSimulated(new GwtTestRunnable() {
            @Override
            public void run() throws Exception {
                assertEquals(11, ItemContainer.getInstance().getItems().size());

                // Select movable
                //ClientSyncItemView movable = getFirstClientSyncItemView(ITEM_MOVABLE);
               // movable.onMouseDown(new TestMouseDownEvent());
                // Move to load range
               // TerrainView.getInstance().onMouseUp(new TestMouseUpEvent(1260, 700, NativeEvent.BUTTON_LEFT));
                executeIfActionServiceIdle(new GwtTestRunnable() {
                    @Override
                    public void run() throws Exception {
                        // Load container
                     //   final ClientSyncItemView transporter = getFirstClientSyncItemView(ITEM_CONTAINER);
                    //    transporter.onMouseDown(new TestMouseDownEvent());

                   /*     executeIfActionServiceIdle(new GwtTestRunnable() {
                            @Override
                            public void run() throws Exception {
                                assertEquals(1, transporter.getClientSyncItem().getSyncBaseItem().getSyncItemContainer().getContainedItems().size());
                                assertEquals(new Index(1450, 700), transporter.getClientSyncItem().getSyncBaseItem().getSyncItemArea().getPosition());

                                transporter.onMouseDown(new TestMouseDownEvent());
                                SideCockpit.getInstance().getCockpitMode().setUnloadMode();
                                //One pixel out of range
                                MapWindow.getInstance().onMouseMove(new TestMouseMoveEvent(1259, 700));
                                assertCursor("url(/images/cursors/nounload.cur), pointer", MapWindow.getAbsolutePanel());
                                TerrainView.getInstance().onMouseUp(new TestMouseUpEvent(1259, 700, NativeEvent.BUTTON_LEFT));
                                assertFalse(ActionHandler.getInstance().isBusy());
                                assertFalse(ItemCockpit.getInstance().isActive());
                                assertEquals(1, transporter.getClientSyncItem().getSyncBaseItem().getSyncItemContainer().getContainedItems().size());

                                //Last valid position
                                SideCockpit.getInstance().getCockpitMode().setUnloadMode();
                                MapWindow.getInstance().onMouseMove(new TestMouseMoveEvent(1260, 700));
                                assertCursor("url(/images/cursors/unload.cur), n-resize", MapWindow.getAbsolutePanel());
                                TerrainView.getInstance().onMouseUp(new TestMouseUpEvent(1260, 700, NativeEvent.BUTTON_LEFT));
                                executeIfActionServiceIdle(new GwtTestRunnable() {
                                    @Override
                                    public void run() throws Exception {
                                        assertEquals(0, transporter.getClientSyncItem().getSyncBaseItem().getSyncItemContainer().getContainedItems().size());
                                        // Test if selection cleared
                                        finishTest();
                                    }
                                });
                            }
                        }); */
                    }
                });
            }
        });
    }


    public void _testLoadTransporter() throws Exception {
        startColdSimulated(new GwtTestRunnable() {
            @Override
            public void run() throws Exception {
                assertEquals(11, ItemContainer.getInstance().getItems().size());

                //TerrainView.getInstance().onMouseDown(new TestMouseDownEvent(900, 100, NativeEvent.BUTTON_LEFT));
                //GroupSelectionFrame.getOnlyForTestGroupSelectionFrame().onMouseMove(new TestMouseMoveEvent(1300, 1600));
                //GroupSelectionFrame.getOnlyForTestGroupSelectionFrame().onMouseUp(new TestMouseUpEvent());
               // ClientSyncItemView transporter = getFirstClientSyncItemView(ITEM_CONTAINER);
                System.out.println("----- Load container");
                //transporter.onMouseDown(new TestMouseDownEvent());
            /*    executeIfActionServiceIdle(new GwtTestRunnable() {
                    @Override
                    public void run() throws Exception {
                        System.out.println("----- Test container loaded");
                        ClientSyncItemView transporter = getFirstClientSyncItemView(ITEM_CONTAINER);
                        List<Id> containingItems = transporter.getClientSyncItem().getSyncBaseItem().getSyncItemContainer().getContainedItems();
                        assertEquals(5, containingItems.size());
                        for (Id id : containingItems) {
                            SyncBaseItem syncBaseItem = (SyncBaseItem) ItemContainer.getInstance().getItem(id);
                            assertFalse(syncBaseItem.getSyncItemArea().hasPosition());
                           // assertNull(ItemContainer.getInstance().getClientSyncItem(syncBaseItem).getClientSyncItemView());
                        }
                        System.out.println("----- Unload container");
                        transporter.onMouseDown(new TestMouseDownEvent());
                        assertTrue("Item Cockpit must be shown now", ItemCockpit.getInstance().isActive());
                        SideCockpit.getInstance().getCockpitMode().setUnloadMode();
                        TerrainView.getInstance().onMouseUp(new TestMouseUpEvent(1300, 800, NativeEvent.BUTTON_LEFT));
                        executeIfActionServiceIdle(new GwtTestRunnable() {
                            @Override
                            public void run() throws Exception {
                                System.out.println("----- Test container unloaded");
                                ClientSyncItemView transporter = getFirstClientSyncItemView(ITEM_CONTAINER);
                                List<Id> containingItems = transporter.getClientSyncItem().getSyncBaseItem().getSyncItemContainer().getContainedItems();
                                assertEquals(0, containingItems.size());
                                for (Id id : containingItems) {
                                    SyncBaseItem syncBaseItem = (SyncBaseItem) ItemContainer.getInstance().getItem(id);
                                    assertTrue(syncBaseItem.getSyncItemArea().hasPosition());
                                //    assertNotNull(ItemContainer.getInstance().getClientSyncItem(syncBaseItem).getClientSyncItemView());
                                }
                                finishTest();
                            }
                        });
                    }
                });  */
            }
        });
    }
}
