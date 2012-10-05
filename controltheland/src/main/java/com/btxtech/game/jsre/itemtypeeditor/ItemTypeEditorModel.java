package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ClientPlanetServices;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.LevelScope;
import com.btxtech.game.jsre.client.common.RadarMode;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.control.task.SimpleDeferredStartup;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.MessageDialog;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.item.ItemTypeContainer;
import com.btxtech.game.jsre.client.renderer.Renderer;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.utg.ClientLevelHandler;
import com.btxtech.game.jsre.common.ImageLoader;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemTypeSpriteMap;
import com.btxtech.game.jsre.common.gameengine.itemType.WeaponType;
import com.btxtech.game.jsre.common.gameengine.services.PlanetInfo;
import com.btxtech.game.jsre.common.gameengine.services.base.BaseAttributes;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemListener;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ItemTypeEditorModel {
    public static final SimpleBase MY_BASE = new SimpleBase(1, PlanetInfo.EDITOR_PLANET_ID);
    public static final SimpleBase ENEMY_BASE = new SimpleBase(2, PlanetInfo.EDITOR_PLANET_ID);
    public static final int SIM_WIDTH = 700;
    public static final int SIM_HEIGHT = 700;

    public interface UpdateListener {
        void onModelUpdate();
    }

    public interface LoadedListener {
        void onModelLoaded();
    }

    private static final ItemTypeEditorModel INSTANCE = new ItemTypeEditorModel();
    private BoundingBox boundingBox;
    private ItemTypeSpriteMap itemTypeSpriteMap;
    private Collection<UpdateListener> updateListeners = new ArrayList<UpdateListener>();
    private Collection<LoadedListener> loadedListeners = new ArrayList<LoadedListener>();
    private ItemTypeImageInfo[][] runtimeImages;
    private ItemTypeImageInfo[][] buildupImages;
    private ItemTypeImageInfo[][][] demolitionImages;
    private int itemTypeId;
    private ItemType itemType;
    private Logger log = Logger.getLogger(ItemTypeEditorModel.class.getName());
    private int currentAngelIndex;
    private boolean initialized;
    private ImageElement spriteMapImageElement;
    private Index simulationMiddle;
    private SyncItem syncItem;
    private boolean moving;
    private Index moveDestination;
    private WeaponType weaponType;
    private SyncBaseItem target;

    public static ItemTypeEditorModel getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private ItemTypeEditorModel() {
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public ItemTypeSpriteMap getItemTypeSpriteMap() {
        return itemTypeSpriteMap;
    }

    public ItemTypeImageInfo[][] getRuntimeImages() {
        return runtimeImages;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setNewAngels(int count) {
        getBoundingBox().setAngels(new double[count]);
        for (int i = 0; i < count; i++) {
            getBoundingBox().getAngels()[i] = MathHelper.ONE_RADIANT * ((double) i / (double) count);
        }
        cutRuntimeToCorrectLength();
        fireUpdate();
    }


    public void setCurrentAngelIndex(int currentAngelIndex) {
        this.currentAngelIndex = currentAngelIndex;
    }

    public int getCurrentAngelIndex() {
        return currentAngelIndex;
    }

    public void increaseCurrentAngelIndex() {
        currentAngelIndex++;
        if (currentAngelIndex >= ItemTypeEditorModel.getInstance().getBoundingBox().getAngelCount()) {
            currentAngelIndex = 0;
        }
        ItemTypeEditorModel.getInstance().fireUpdate();
        setTargetPosition();
        if (moving) {
            executeMoveCommand();
        }
    }

    public void decreaseCurrentAngelIndex() {
        currentAngelIndex--;
        if (currentAngelIndex < 0) {
            currentAngelIndex = ItemTypeEditorModel.getInstance().getBoundingBox().getAngelCount() - 1;
        }
        ItemTypeEditorModel.getInstance().fireUpdate();
        setTargetPosition();
        if (moving) {
            executeMoveCommand();
        }
    }

    public DropImage getDropImage(int angelIndex, int step, int animationFrame, ItemTypeSpriteMap.SyncObjectState syncObjectState) {
        if (isImageOverridden(angelIndex, step, animationFrame, syncObjectState)) {
            ItemTypeImageInfo imageInfo;
            switch (syncObjectState) {
                case BUILD_UP:
                    imageInfo = buildupImages[step][animationFrame];
                    break;
                case RUN_TIME:
                    imageInfo = runtimeImages[angelIndex][animationFrame];
                    break;
                case DEMOLITION:
                    imageInfo = demolitionImages[angelIndex][step][animationFrame];
                    break;
                default:
                    throw new IllegalArgumentException("ItemTypeEditorModel.getImage() unknown SyncObjectState1: " + syncObjectState);
            }
            return new DropImage(imageInfo.getBase64ImageData(), angelIndex, step, animationFrame, syncObjectState);
        } else {
            if (spriteMapImageElement != null) {
                Index offset;
                switch (syncObjectState) {
                    case BUILD_UP:
                        offset = itemTypeSpriteMap.getBuildupImageOffsetFromFrame(step, animationFrame);
                        break;
                    case RUN_TIME:
                        offset = itemTypeSpriteMap.getRuntimeImageOffsetFromFrame(angelIndex, animationFrame);
                        break;
                    case DEMOLITION:
                        offset = itemTypeSpriteMap.getDemolitionImageOffsetFromFrame(angelIndex, step, animationFrame);
                        break;
                    default:
                        throw new IllegalArgumentException("ItemTypeEditorModel.getImage() unknown SyncObjectState2: " + syncObjectState);
                }
                return new DropImage(spriteMapImageElement.getSrc(),
                        offset.getX(), offset.getY(),
                        itemTypeSpriteMap.getImageWidth(), itemTypeSpriteMap.getImageHeight(),
                        angelIndex, step, animationFrame, syncObjectState);
            } else {
                DropImage dropImage = new DropImage("", angelIndex, step, animationFrame, syncObjectState);
                dropImage.setPixelSize(20, 20);
                dropImage.getElement().getStyle().setBackgroundColor("#CCCCCC");
                return dropImage;
            }
        }
    }

    public void cutRuntimeToCorrectLength() {
        runtimeImages = new ItemTypeImageInfo[boundingBox.getAngelCount()][itemTypeSpriteMap.getRuntimeAnimationFrames()];
        fireUpdate();
    }

    public void cutBuildupToCorrectLength() {
        buildupImages = new ItemTypeImageInfo[itemTypeSpriteMap.getBuildupSteps()][itemTypeSpriteMap.getBuildupAnimationFrames()];
        fireUpdate();
    }

    public void cutDemolitionToCorrectLength() {
        demolitionImages = new ItemTypeImageInfo[boundingBox.getAngelCount()][itemTypeSpriteMap.getDemolitionSteps()][itemTypeSpriteMap.getDemolitionAnimationFrames()];
        fireUpdate();
    }

    public void overrideImages(boolean overrideFrame, int angelIndex, int step, int frame, ItemTypeSpriteMap.SyncObjectState syncObjectState, List<String> base64ImageDatas) {
        switch (syncObjectState) {
            case BUILD_UP:
                overrideBuildupImages(overrideFrame, step, frame, base64ImageDatas);
                break;
            case RUN_TIME:
                overrideRuntimeImages(overrideFrame, angelIndex, frame, base64ImageDatas);
                break;
            case DEMOLITION:
                overrideDemolitionImages(overrideFrame, angelIndex, step, frame, base64ImageDatas);
                break;
            default:
                throw new IllegalArgumentException("DropImage.finalizeDrop() unknown syncObjectState: " + syncObjectState);
        }
    }

    private void overrideDemolitionImages(boolean overrideFrame, int angelIndex, int step, int frame, List<String> base64ImageDatas) {
        overrideImageSize(base64ImageDatas);
        for (int i = 0; i < base64ImageDatas.size(); i++) {
            if (overrideFrame) {
                if (frame + i >= demolitionImages[angelIndex][step].length) {
                    return;
                }
                demolitionImages[angelIndex][step][frame + i] = new ItemTypeImageInfo(angelIndex, step, frame + i, base64ImageDatas.get(i));
            } else {
                if (angelIndex + i >= demolitionImages.length) {
                    return;
                }
                demolitionImages[angelIndex + i][step][frame] = new ItemTypeImageInfo(angelIndex + i, step, frame, base64ImageDatas.get(i));
            }
        }
        fireUpdate();
    }

    private void overrideBuildupImages(boolean overrideFrame, int step, int frame, List<String> base64ImageDatas) {
        overrideImageSize(base64ImageDatas);
        for (int i = 0; i < base64ImageDatas.size(); i++) {
            if (overrideFrame) {
                if (frame + i >= buildupImages[step].length) {
                    return;
                }
                buildupImages[step][frame + i] = new ItemTypeImageInfo(0, step, frame + i, base64ImageDatas.get(i));
            } else {
                if (step + i >= buildupImages.length) {
                    return;
                }
                buildupImages[step + i][frame] = new ItemTypeImageInfo(0, step + i, frame, base64ImageDatas.get(i));
            }
        }
        fireUpdate();
    }

    private void overrideRuntimeImages(boolean overrideFrame, int angelIndex, int frame, List<String> base64ImageDatas) {
        overrideImageSize(base64ImageDatas);
        for (int i = 0; i < base64ImageDatas.size(); i++) {
            if (overrideFrame) {
                if (frame + i >= runtimeImages[angelIndex].length) {
                    return;
                }
                runtimeImages[angelIndex][frame + i] = new ItemTypeImageInfo(angelIndex, 0, frame + i, base64ImageDatas.get(i));
            } else {
                if (angelIndex + i >= runtimeImages.length) {
                    return;
                }
                runtimeImages[angelIndex + i][frame] = new ItemTypeImageInfo(angelIndex + i, 0, frame, base64ImageDatas.get(i));
            }
        }
        fireUpdate();
    }

    private void overrideImageSize(List<String> base64ImageDatas) {
        if (base64ImageDatas.size() > 0) {
            final Image image = new Image(base64ImageDatas.get(0));
            if (image.getWidth() == 0 || image.getHeight() == 0) {
                image.addLoadHandler(new LoadHandler() {
                    @Override
                    public void onLoad(LoadEvent event) {
                        itemTypeSpriteMap.setImageWidth(image.getWidth());
                        itemTypeSpriteMap.setImageHeight(image.getHeight());
                        RootPanel.get().remove(image);
                    }
                });
                RootPanel.get().add(image);
            } else {
                itemTypeSpriteMap.setImageWidth(image.getWidth());
                itemTypeSpriteMap.setImageHeight(image.getHeight());
            }
        }
    }

    public void fireUpdate() {
        if (!initialized) {
            return;
        }
        if (currentAngelIndex >= ItemTypeEditorModel.getInstance().getBoundingBox().getAngelCount()) {
            currentAngelIndex = 0;
        }
        for (UpdateListener updateListener : updateListeners) {
            updateListener.onModelUpdate();
        }
    }

    public void addUpdateListener(UpdateListener updateListener) {
        updateListeners.add(updateListener);
    }

    public void addLoadedListener(LoadedListener loadedListener) {
        loadedListeners.add(loadedListener);
    }

    public void clearUpdateListeners() {
        updateListeners.clear();
    }

    public boolean isImageOverridden(int angelIndex, int step, int frame, ItemTypeSpriteMap.SyncObjectState syncObjectState) {
        switch (syncObjectState) {
            case BUILD_UP:
                return buildupImages[step][frame] != null;
            case RUN_TIME:
                return runtimeImages.length > 0 && runtimeImages[0].length > 0 && runtimeImages[angelIndex][frame] != null;
            case DEMOLITION:
                return demolitionImages[angelIndex][step][frame] != null;
            default:
                throw new IllegalArgumentException("ItemTypeEditorModel.isImageOverridden() unknown syncObjectState: " + syncObjectState);
        }

    }

    public ImageElement getImageElement(int angelIndex, int step, int frame, ItemTypeSpriteMap.SyncObjectState syncObjectState) {
        if (!isImageOverridden(angelIndex, step, frame, syncObjectState)) {
            throw new IllegalArgumentException("ItemTypeEditorModel.getImageElement() is not overridden. angelIndex=" + angelIndex + " step=" + step + " frame=" + frame + " syncObjectState=" + syncObjectState);
        }
        Image image;
        switch (syncObjectState) {
            case BUILD_UP:
                image = new Image(buildupImages[step][frame].getBase64ImageData());
                break;
            case RUN_TIME:
                image = new Image(runtimeImages[angelIndex][frame].getBase64ImageData());
                break;
            case DEMOLITION:
                image = new Image(demolitionImages[angelIndex][step][frame].getBase64ImageData());
                break;
            default:
                throw new IllegalArgumentException("ItemTypeEditorModel.isImageOverridden() unknown syncObjectState: " + syncObjectState);
        }
        return ImageElement.as(image.getElement());
    }

    public ImageElement getSpriteMapImageElement() {
        return spriteMapImageElement;
    }

    private void loadSpriteMap(final int itemTypeId) {
        ImageLoader<Integer> itemTypeImageLoader = new ImageLoader<Integer>();
        itemTypeImageLoader.addImageUrl(ImageHandler.getItemTypeSpriteMapUrl(itemTypeId), itemTypeId);
        itemTypeImageLoader.startLoading(new ImageLoader.Listener<Integer>() {
            @Override
            public void onLoaded(Map<Integer, ImageElement> imageElements) {
                spriteMapImageElement = imageElements.get(itemTypeId);
                fireUpdate();
            }
        });
    }

    public void saveItemType(final Button button) {
        button.setEnabled(false);
        ItemTypeAccessAsync itemTypeAccess = GWT.create(ItemTypeAccess.class);
        itemTypeAccess.saveItemTypeProperties(itemTypeId,
                boundingBox,
                itemTypeSpriteMap,
                weaponType,
                generateBuildupImageCollection(),
                generateRuntimeImageCollection(),
                generateDemolitionImageCollection(),
                new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        button.setEnabled(true);
                        log.log(Level.SEVERE, "saveItemTypeProperties call failed", caught);
                        DialogManager.showDialog(new MessageDialog("Failure", "Save failed! " + caught.getMessage()), DialogManager.Type.PROMPTLY);
                    }

                    @Override
                    public void onSuccess(Void result) {
                        button.setEnabled(true);
                    }
                });
    }

    private Collection<ItemTypeImageInfo> generateBuildupImageCollection() {
        Collection<ItemTypeImageInfo> buildupImages = new ArrayList<ItemTypeImageInfo>();
        for (ItemTypeImageInfo[] buildupImageArray : this.buildupImages) {
            for (ItemTypeImageInfo itemTypeImageInfo : buildupImageArray) {
                if (itemTypeImageInfo != null) {
                    buildupImages.add(itemTypeImageInfo);
                }
            }
        }
        return buildupImages;
    }

    private Collection<ItemTypeImageInfo> generateRuntimeImageCollection() {
        Collection<ItemTypeImageInfo> runtimeImages = new ArrayList<ItemTypeImageInfo>();
        for (ItemTypeImageInfo[] runtimeImageArray : this.runtimeImages) {
            for (ItemTypeImageInfo itemTypeImageInfo : runtimeImageArray) {
                if (itemTypeImageInfo != null) {
                    runtimeImages.add(itemTypeImageInfo);
                }
            }
        }
        return runtimeImages;
    }

    private Collection<ItemTypeImageInfo> generateDemolitionImageCollection() {
        Collection<ItemTypeImageInfo> demolitionImages = new ArrayList<ItemTypeImageInfo>();
        for (ItemTypeImageInfo[][] demolitionImageField : this.demolitionImages) {
            for (ItemTypeImageInfo[] demolitionImageArray : demolitionImageField) {
                for (ItemTypeImageInfo itemTypeImageInfo : demolitionImageArray) {
                    if (itemTypeImageInfo != null) {
                        demolitionImages.add(itemTypeImageInfo);
                    }
                }
            }
        }
        return demolitionImages;
    }

    public void loadItemType(int itemTypeId) {
        this.itemTypeId = itemTypeId;
        loadSpriteMap(itemTypeId);
        ItemTypeAccessAsync itemTypeAccess = GWT.create(ItemTypeAccess.class);
        itemTypeAccess.getItemType(itemTypeId, new AsyncCallback<ItemType>() {

            @Override
            public void onFailure(Throwable caught) {
                log.log(Level.SEVERE, "createBoundingBox call failed", caught);
            }

            @Override
            public void onSuccess(ItemType itemType) {
                ItemTypeEditorModel.this.itemType = itemType;
                try {
                    ///--- Init Model
                    boundingBox = itemType.getBoundingBox();
                    itemTypeSpriteMap = itemType.getItemTypeSpriteMap();
                    cutBuildupToCorrectLength();
                    cutRuntimeToCorrectLength();
                    cutDemolitionToCorrectLength();
                    ///--- Setup Base
                    ClientBase.getInstance().setBase(MY_BASE);
                    Collection<BaseAttributes> allBaseAttributes = new ArrayList<BaseAttributes>();
                    allBaseAttributes.add(new BaseAttributes(MY_BASE, "MyBase", false));
                    allBaseAttributes.add(new BaseAttributes(ENEMY_BASE, "Enemy", false));
                    ClientBase.getInstance().setAllBaseAttributes(allBaseAttributes);
                    ///--- Setup div
                    TerrainView.uglySuppressRadar = true;
                    Connection.getInstance().init4ItemTypeEditor();
                    PlanetInfo planetInfo = new PlanetInfo();
                    planetInfo.setRadarMode(RadarMode.NONE);
                    planetInfo.setPlanetId(PlanetInfo.EDITOR_PLANET_ID);
                    ClientPlanetServices.getInstance().setPlanetInfo(planetInfo);
                    ClientLevelHandler.getInstance().setLevel(new LevelScope(PlanetInfo.EDITOR_PLANET_ID, 0, 0, null, 0));
                    ///--- Setup terrain
                    ArrayList<SurfaceImage> surfaceImages = new ArrayList<SurfaceImage>();
                    if (itemType.getTerrainType() == null) {
                        throw new IllegalArgumentException("ItemTypeEditorModel: No surface type specified");
                    }
                    surfaceImages.add(new SurfaceImage(itemType.getTerrainType().getSurfaceTypes().get(0), 23, "#00FF00"));
                    ArrayList<SurfaceRect> surfaceRects = new ArrayList<SurfaceRect>();
                    surfaceRects.add(new SurfaceRect(new Rectangle(0, 0, SIM_WIDTH / 100, SIM_HEIGHT / 100), 23));
                    TerrainView.getInstance().setupTerrain(new TerrainSettings(SIM_WIDTH / 100, SIM_HEIGHT / 100),
                            new ArrayList<TerrainImagePosition>(),
                            surfaceRects,
                            surfaceImages,
                            new ArrayList<TerrainImage>(),
                            null);
                    TerrainView.getInstance().getTerrainHandler().loadImagesAndDrawMap(new SimpleDeferredStartup());
                    TerrainView.getInstance().addToParent(MapWindow.getAbsolutePanel());
                    TerrainView.getInstance().getTerrainHandler().createTerrainTileField(Collections.<TerrainImagePosition>emptyList(), surfaceRects);
                    Renderer.getInstance().overrideItemRenderTask(new ItemEditorItemRenderTask(TerrainView.getInstance().getContext2d()));
                    ///--- Setup Item Container
                    Collection<ItemType> itemTypes = new ArrayList<ItemType>();
                    itemTypes.add(itemType);
                    ItemTypeContainer.getInstance().setItemTypes(itemTypes);
                    // Item
                    createSyncItem();
                    // Finish initialisation
                    initialized = true;
                    for (LoadedListener loadedListener : loadedListeners) {
                        loadedListener.onModelLoaded();
                    }
                    fireUpdate();

                } catch (Throwable t) {
                    log.log(Level.SEVERE, "Can not start ItemTypeEditor", t);
                }
            }
        });
    }

    private void createSyncItem() {
        try {
            simulationMiddle = new Index(SIM_WIDTH / 2, SIM_HEIGHT / 2);
            SimpleBase myBase = null;
            if (itemType instanceof BaseItemType) {
                myBase = MY_BASE;
            }
            syncItem = ItemContainer.getInstance().createItemTypeEditorSyncObject(myBase, itemType.getId(), simulationMiddle);
            syncItem.addSyncItemListener(new SyncItemListener() {
                @Override
                public void onItemChanged(Change change, SyncItem syncItem) {
                    if (moving && change == Change.POSITION && moveDestination != null && moveDestination.equals(syncItem.getSyncItemArea().getPosition())) {
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
                SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
                weaponType = syncBaseItem.getBaseItemType().getWeaponType();
                if (syncBaseItem.hasSyncConsumer()) {
                    syncBaseItem.getSyncConsumer().setOperationState(true);
                }
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "", e);
        }
    }

    public SyncItem getSyncItem() {
        return syncItem;
    }

    public WeaponType getWeaponType() {
        return weaponType;
    }

    public void executeMoveCommand() {
        stopAttack();
        if (syncItem instanceof SyncBaseItem && ((SyncBaseItem) syncItem).hasSyncMovable()) {
            moving = true;
            syncItem.getSyncItemArea().setPosition(simulationMiddle);
            double angel = boundingBox.angelIndexToAngel(currentAngelIndex);
            moveDestination = simulationMiddle.getPointFromAngelToNord(angel, 200);
            if (moveDestination.getX() > SIM_WIDTH - 1) {
                moveDestination.setX(SIM_WIDTH - 1);
            }
            if (moveDestination.getY() > SIM_HEIGHT - 1) {
                moveDestination.setY(SIM_HEIGHT - 1);
            }
            moveDestination = Index.createSaveIndex(moveDestination);
            ActionHandler.getInstance().moveItemTypeEditor((SyncBaseItem) syncItem, moveDestination, angel);
        }
    }

    private void stopMove() {
        moving = false;
        if (syncItem instanceof SyncBaseItem && ((SyncBaseItem) syncItem).hasSyncMovable()) {
            ((SyncBaseItem) syncItem).getSyncMovable().setPathToDestination(null, 0.0);
        }
        syncItem.getSyncItemArea().setPosition(simulationMiddle);
    }

    public void doAttack() {
        stopMove();
        if (weaponType != null) {
            if (target == null || !target.isAlive()) {
                try {
                    target = (SyncBaseItem) ItemContainer.getInstance().createItemTypeEditorSyncObject(ENEMY_BASE, itemTypeId, new Index(250, 150));
                    target.setHealth(1000000);
                } catch (NoSuchItemTypeException e) {
                    log.log(Level.SEVERE, "", e);
                }
            }
            target.setHealth(1000000);
            ((SyncBaseItem) syncItem).setHealth(1000000);
            setTargetPosition();
            ActionHandler.getInstance().attack((SyncBaseItem) syncItem,
                    target,
                    syncItem.getSyncItemArea().getPosition(),
                    0,
                    false);
        }
    }

    private void stopAttack() {
        if (target != null && target.isAlive()) {
            ItemContainer.getInstance().killSyncItem(target, null, true, false);
        }
    }

    private void setTargetPosition() {
        if (target != null) {
            double angel = boundingBox.angelIndexToAngel(currentAngelIndex);
            Index targetPos = syncItem.getSyncItemArea().getPosition().getPointFromAngelToNord(angel, (double) (weaponType.getRange() + 2 * boundingBox.getRadius()) * 0.75);
            targetPos = Index.createSaveIndex(targetPos);
            target.getSyncItemArea().setPosition(targetPos);
        }
    }

    public void setBaseItemTypeBuildup(double buildup) {
        stopAttack();
        stopMove();
        if (syncItem instanceof SyncBaseItem) {
            ((SyncBaseItem) syncItem).setBuildup(buildup);
        }
    }

    public void setBaseItemTypeDemolition(double demolition) {
        stopAttack();
        stopMove();
        if (syncItem instanceof SyncBaseItem) {
            ((SyncBaseItem) syncItem).setHealth((double) ((SyncBaseItem) syncItem).getBaseItemType().getHealth() * demolition);
        }
    }

}
