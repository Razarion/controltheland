package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.collision.ClientCollisionService;
import com.btxtech.game.jsre.client.common.LevelScope;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.control.task.SimpleDeferredStartup;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.utg.ClientLevelHandler;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
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
    private static final int SIM_WIDTH = 600;
    private static final int SIM_HEIGHT = 800;
    public static final SimpleBase MY_BASE = new SimpleBase(1);
    public static final SimpleBase ENEMY_BASE = new SimpleBase(2);
    private Logger log = Logger.getLogger(ItemTypeEditorPanel.class.getName());
    private int itemTypeId;
    private ItemTypeSimulation itemTypeSimulation;
    private MuzzleFlashControl muzzleFlashControl;

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
                try {
                    ///--- Setup div
                    TerrainView.uglySuppressRadar = true;
                    Connection.getInstance().init4ItemTypeEditor();
                    ClientLevelHandler.getInstance().setLevelScope(new LevelScope(0, null, 0, 0));
                    ///--- Setup terrain
                    ArrayList<SurfaceImage> surfaceImages = new ArrayList<SurfaceImage>();
                    surfaceImages.add(new SurfaceImage(itemType.getTerrainType().getSurfaceTypes().get(0), 23, "#00FF00"));
                    ArrayList<SurfaceRect> surfaceRects = new ArrayList<SurfaceRect>();
                    surfaceRects.add(new SurfaceRect(new Rectangle(0, 0, SIM_WIDTH / 100, SIM_HEIGHT / 100), 23));
                    TerrainView.getInstance().setupTerrain(new TerrainSettings(SIM_WIDTH / 100, SIM_HEIGHT / 100, 100, 100),
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
                    if (itemType instanceof BaseItemType) {
                        itemTypeSimulation.createSyncItem();
                    }
                    //---- Init the editors
                    muzzleFlashControl.init(0, itemType);
                } catch (Throwable t) {
                    log.log(Level.SEVERE, "Can not start ItemTypeEditor", t);
                }
            }
        });
    }

    private void setupGui(ItemType itemType) {
        // Create panels
        muzzleFlashControl = new MuzzleFlashControl();
        BoundingBoxControl boundingBoxControl = new BoundingBoxControl(itemTypeId, itemType.getBoundingBox(), muzzleFlashControl);
        itemTypeSimulation = new ItemTypeSimulation(SIM_WIDTH, SIM_HEIGHT, itemType, muzzleFlashControl);
        ItemTypeView itemTypeView = new ItemTypeView(300, 300, itemType, boundingBoxControl, muzzleFlashControl);
        RotationControl rotationControl = new RotationControl(itemType.getBoundingBox(), itemTypeView, itemTypeSimulation, muzzleFlashControl);
        muzzleFlashControl.setRotationControl(rotationControl);
        BuildupStepEditorPanel buildupStepEditorPanel = new BuildupStepEditorPanel(itemType, itemTypeSimulation, boundingBoxControl);
        // Init panels
        boundingBoxControl.setRotationControl(rotationControl);
        // Add panels to main panel
        setWidget(0, 0, itemTypeView);
        getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
        getFlexCellFormatter().setRowSpan(0, 0, 4);
        setWidget(0, 1, rotationControl);
        // Col is 0 (only one col in second row)
        setWidget(1, 0, boundingBoxControl);
        setWidget(2, 0, muzzleFlashControl);
        setWidget(3, 0, buildupStepEditorPanel);
        getFlexCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);
        setWidget(0, 2, MapWindow.getAbsolutePanel());
        getFlexCellFormatter().setRowSpan(0, 2, 4);
        MapWindow.getInstance().setMinimalSize(SIM_WIDTH, SIM_HEIGHT);
    }
}
