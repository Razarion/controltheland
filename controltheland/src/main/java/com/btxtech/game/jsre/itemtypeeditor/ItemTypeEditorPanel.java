package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.collision.ClientCollisionService;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.control.task.SimpleDeferredStartup;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.utg.ClientLevelHandler;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 15.08.2011
 * Time: 20:27:37
 */
public class ItemTypeEditorPanel extends FlexTable {
    public static final SimpleBase MY_BASE = new SimpleBase(1);
    public static final SimpleBase ENEMY_BASE = new SimpleBase(2);
    private Logger log = Logger.getLogger(ItemTypeEditorPanel.class.getName());
    private int itemTypeId;
    private ItemTypeSimulation itemTypeSimulation;

    public ItemTypeEditorPanel(int itemTypeId) {
        this.itemTypeId = itemTypeId;
        ItemTypeAccessAsync itemTypeAccess = GWT.create(ItemTypeAccess.class);
        itemTypeAccess.getItemType(itemTypeId, new AsyncCallback<ItemType>() {
            @Override
            public void onFailure(Throwable caught) {
                log.log(Level.SEVERE, "getBoundingBox call failed", caught);
            }

            @Override
            public void onSuccess(ItemType itemType) {
                ///--- Setup div
                TerrainView.uglySuppressRadar = true;
                Connection.getInstance().init4ItemTypeEditor();
                ClientLevelHandler.getInstance().setLevel(new com.btxtech.game.jsre.client.common.Level("ItemTypeEditorLevel", "", false, 0, null, 0));
                ///--- Setup terrain
                ArrayList<SurfaceImage> surfaceImages = new ArrayList<SurfaceImage>();
                surfaceImages.add(new SurfaceImage(SurfaceType.LAND, 23, "#00FF00"));
                ArrayList<SurfaceRect> surfaceRects = new ArrayList<SurfaceRect>();
                surfaceRects.add(new SurfaceRect(new Rectangle(0, 0, 5, 5), 23));
                TerrainView.getInstance().setupTerrain(new TerrainSettings(5, 5, 100, 100),
                        new ArrayList<TerrainImagePosition>(),
                        surfaceRects,
                        surfaceImages,
                        new ArrayList<TerrainImage>());
                TerrainView.getInstance().getTerrainHandler().loadImagesAndDrawMap(new SimpleDeferredStartup());
                TerrainView.getInstance().addToParent(MapWindow.getAbsolutePanel());
                ClientCollisionService.getInstance().setup();
                ///--- Setup Item Container
                Collection<ItemType> itemTypes = new ArrayList<ItemType>();
                itemTypes.add(itemType);
                ItemContainer.getInstance().setItemTypes(itemTypes);
                ///--- Setup Base
                ClientBase.getInstance().setBase(MY_BASE);
                setupGui(itemType);
                itemTypeSimulation.createSyncItem();
            }
        });
    }

    private void setupGui(ItemType itemType) {
        // Create panels
        BoundingBoxControl boundingBoxControl = new BoundingBoxControl(itemTypeId, itemType.getBoundingBox());
        itemTypeSimulation = new ItemTypeSimulation(500, 500, itemType);
        ItemTypeView itemTypeView = new ItemTypeView(300, 300, itemType, boundingBoxControl);
        MuzzleFlashControl muzzleFlashControl = new MuzzleFlashControl();
        RotationControl rotationControl = new RotationControl(itemType.getBoundingBox(), itemTypeView, itemTypeSimulation, muzzleFlashControl);
        // Init panels
        boundingBoxControl.setRotationControl(rotationControl);
        // Add panels to main panel
        setWidget(0, 0, itemTypeView);
        getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
        getFlexCellFormatter().setRowSpan(0, 0, 3);
        setWidget(0, 1, rotationControl);
        // Col is 0 (only one col in second row)
        setWidget(1, 0, boundingBoxControl);
        setWidget(2, 0, muzzleFlashControl);
        getFlexCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);
        setWidget(0, 2, MapWindow.getAbsolutePanel());
        getFlexCellFormatter().setRowSpan(0, 2, 3);
        MapWindow.getInstance().setMinimalSize(500, 500);
    }
}
