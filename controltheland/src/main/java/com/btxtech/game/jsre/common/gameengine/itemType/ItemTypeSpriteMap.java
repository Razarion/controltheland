package com.btxtech.game.jsre.common.gameengine.itemType;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * User: beat
 * Date: 01.08.12
 * Time: 13:43
 */
public class ItemTypeSpriteMap implements Serializable {
    public static enum SyncObjectState {
        BUILD_UP,
        RUN_TIME,
        DEMOLITION
    }

    private int imageWidth;
    private int imageHeight;
    private int buildupSteps;
    private int buildupAnimationFrames;
    private int buildupAnimationDuration;
    private int runtimeXOffset;
    private int runtimeAnimationFrames;
    private int runtimeAnimationDuration;
    private int demolitionXOffset;
    private int demolitionSteps;
    private int demolitionAnimationFrames;
    private int demolitionAnimationDuration;
    private Map<Integer, Collection<ItemClipPosition>> demolitionStepClips;
    private BoundingBox boundingBox;
    private Index cosmeticImageOffset;
    private int spriteWidth;
    private int spriteHeight;

    /**
     * Used by GWT
     */
    ItemTypeSpriteMap() {
    }

    public ItemTypeSpriteMap(BoundingBox boundingBox,
                             int imageWidth,
                             int imageHeight,
                             int buildupSteps,
                             int buildupAnimationFrames,
                             int buildupAnimationDuration,
                             int runtimeAnimationFrames,
                             int runtimeAnimationDuration,
                             int demolitionSteps,
                             int demolitionAnimationFrames,
                             int demolitionAnimationDuration, Map<Integer, Collection<ItemClipPosition>> demolitionStepClips) {
        this.boundingBox = boundingBox;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.buildupSteps = buildupSteps;
        this.buildupAnimationFrames = buildupAnimationFrames;
        this.buildupAnimationDuration = buildupAnimationDuration;
        this.runtimeAnimationFrames = runtimeAnimationFrames;
        this.runtimeAnimationDuration = runtimeAnimationDuration;
        this.demolitionSteps = demolitionSteps;
        this.demolitionAnimationFrames = demolitionAnimationFrames;
        this.demolitionAnimationDuration = demolitionAnimationDuration;
        this.demolitionStepClips = demolitionStepClips;
        runtimeXOffset = imageWidth * buildupSteps * buildupAnimationFrames;
        demolitionXOffset = runtimeXOffset + imageWidth * boundingBox.getAngelCount() * runtimeAnimationFrames;
        cosmeticImageOffset = getRuntimeImageOffset(boundingBox.getCosmeticAngelIndex(), 0);
        spriteWidth = imageWidth * (buildupSteps * buildupAnimationFrames + boundingBox.getAngelCount() * runtimeAnimationFrames + boundingBox.getAngelCount() * demolitionSteps * demolitionAnimationFrames);
        spriteHeight = imageHeight;
    }

    public SyncObjectState getSyncObjectState(SyncItem syncItem) {
        if (syncItem instanceof SyncBaseItem) {
            SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
            if (!syncBaseItem.isReady()) {
                if (buildupSteps > 0) {
                    return SyncObjectState.BUILD_UP;
                } else {
                    return SyncObjectState.RUN_TIME;
                }
            } else if (syncBaseItem.isHealthy()) {
                return SyncObjectState.RUN_TIME;
            } else {
                if (demolitionSteps > 0) {
                    return SyncObjectState.DEMOLITION;
                } else {
                    return SyncObjectState.RUN_TIME;
                }
            }
        } else {
            return SyncObjectState.RUN_TIME;
        }
    }

    public Index getItemTypeImageOffset(SyncItem syncItem, long timeStamp) {
        switch (getSyncObjectState(syncItem)) {
            case BUILD_UP:
                return getBuildupImageOffset((SyncBaseItem) syncItem, timeStamp);
            case RUN_TIME:
                return getRuntimeImageOffset(syncItem, timeStamp);
            case DEMOLITION:
                return getDemolitionImageOffset((SyncBaseItem) syncItem, timeStamp);
            default:
                throw new IllegalArgumentException("ItemTypeSpriteMap.getItemTypeImageOffset() unknown SyncObjectState: " + getSyncObjectState(syncItem));
        }
    }

    private Index getBuildupImageOffset(SyncBaseItem syncBaseItem, long timeStamp) {
        int step = getBuildupStep(syncBaseItem);
        int animationFrame = getBuildupAnimationFrame(timeStamp);
        return getBuildupImageOffsetFromFrame(step, animationFrame);
    }

    public Index getBuildupImageOffsetFromFrame(int step, int animationFrame) {
        return new Index(imageWidth * (step * buildupAnimationFrames + animationFrame), 0);
    }

    public int getBuildupAnimationFrame(long timeStamp) {
        int animationFrame = 0;
        if (buildupAnimationDuration > 0) {
            long iteration = timeStamp / buildupAnimationDuration;
            animationFrame = (int) (iteration % buildupAnimationFrames);
        }
        return animationFrame;
    }

    public int getBuildupStep(SyncItem syncItem) {
        if (syncItem instanceof SyncBaseItem) {
            return (int) (((SyncBaseItem) syncItem).getBuildup() * buildupSteps);
        } else {
            return 0;
        }
    }

    private Index getRuntimeImageOffset(SyncItem syncItem, long timeStamp) {
        int angelIndex = boundingBox.angelToAngelIndex(syncItem.getSyncItemArea().getAngel());
        return getRuntimeImageOffset(angelIndex, timeStamp);
    }

    public Index getRuntimeImageOffset(int angelIndex, long timeStamp) {
        int animationFrame = getRuntimeAnimationFrame(timeStamp);
        return getRuntimeImageOffsetFromFrame(angelIndex, animationFrame);
    }

    public int getRuntimeAnimationFrame(long timeStamp) {
        int animationFrame = 0;
        if (runtimeAnimationDuration > 0) {
            long iteration = timeStamp / runtimeAnimationDuration;
            animationFrame = (int) (iteration % runtimeAnimationFrames);
        }
        return animationFrame;
    }

    public Index getRuntimeImageOffsetFromFrame(int angelIndex, int frame) {
        return new Index(runtimeXOffset + imageWidth * (angelIndex * runtimeAnimationFrames + frame), 0);
    }

    public Index getDemolitionImageOffsetFromFrame(int angelIndex, int step, int animationFrame) {
        return new Index(demolitionXOffset + imageWidth * (demolitionAnimationFrames * (angelIndex * demolitionSteps + step) + animationFrame), 0);
    }

    public Index getDemolitionImageOffset(SyncBaseItem syncBaseItem, long timeStamp) {
        int step = getDemolitionStep(syncBaseItem);
        int angelIndex = boundingBox.angelToAngelIndex(syncBaseItem.getSyncItemArea().getAngel());
        int animationFrame = getDemolitionAnimationFrame(timeStamp);
        return getDemolitionImageOffsetFromFrame(angelIndex, step, animationFrame);
    }

    public int getDemolitionAnimationFrame(long timeStamp) {
        int animationFrame = 0;
        if (demolitionAnimationDuration > 0) {
            long iteration = timeStamp / demolitionAnimationDuration;
            animationFrame = (int) (iteration % demolitionAnimationFrames);
        }
        return animationFrame;
    }

    public int getDemolitionStep(SyncItem syncItem) {
        if (syncItem instanceof SyncBaseItem) {
            return (int) (demolitionSteps * (1.0 - ((SyncBaseItem) syncItem).getNormalizedHealth()));
        } else {
            return 0;
        }
    }

    public Index getCosmeticImageOffset() {
        return cosmeticImageOffset;
    }

    /* *
     * @param angel angel
     * @return offset = ImageNr * image imageWidth
     */
/*    public int angelToImageOffset(double angel) {
        return angelToImageNr(angel) * imageWidth;
    } */
    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public Index getMiddleFromImage() {
        return new Index(imageWidth / 2, imageHeight / 2);
    }

    /**
     * The cosmetic image index starts with 0.
     *
     * @return The cosmetic image index starts with 0.
     */
    /*   public int getCosmeticImageIndex() {
        return getCosmeticImageIndex(boundingBox.getAngels().length);
    }*/
    public int getBuildupSteps() {
        return buildupSteps;
    }

    public void setBuildupSteps(int buildupSteps) {
        this.buildupSteps = buildupSteps;
    }

    public int getBuildupAnimationFrames() {
        return buildupAnimationFrames;
    }

    public void setBuildupAnimationFrames(int buildupAnimationFrames) {
        this.buildupAnimationFrames = buildupAnimationFrames;
    }

    public int getRuntimeAnimationFrames() {
        return runtimeAnimationFrames;
    }

    public int getDemolitionAnimationFrames() {
        return demolitionAnimationFrames;
    }

    public void setDemolitionAnimationFrames(int demolitionAnimationFrames) {
        this.demolitionAnimationFrames = demolitionAnimationFrames;
    }

    public int getDemolitionSteps() {
        return demolitionSteps;
    }

    public void setDemolitionSteps(int demolitionSteps) {
        this.demolitionSteps = demolitionSteps;
    }

    public int getDemolitionAnimationDuration() {
        return demolitionAnimationDuration;
    }

    public void setDemolitionAnimationDuration(int demolitionAnimationDuration) {
        this.demolitionAnimationDuration = demolitionAnimationDuration;
    }

    public int getRuntimeAnimationDuration() {
        return runtimeAnimationDuration;
    }

    public void setRuntimeAnimationFrames(int runtimeAnimationFrames) {
        this.runtimeAnimationFrames = runtimeAnimationFrames;
    }

    public void setRuntimeAnimationDuration(int runtimeAnimationDuration) {
        this.runtimeAnimationDuration = runtimeAnimationDuration;
    }

    public int getBuildupAnimationDuration() {
        return buildupAnimationDuration;
    }

    public void setBuildupAnimationDuration(int buildupAnimationDuration) {
        this.buildupAnimationDuration = buildupAnimationDuration;
    }

    public int getSpriteWidth() {
        return spriteWidth;
    }

    public int getSpriteHeight() {
        return spriteHeight;
    }

    public Collection<ItemClipPosition> getDemolitionClipIds(SyncBaseItem syncBaseItem) {
        if (demolitionStepClips == null) {
            return null;
        }
        int demolitionStep = getDemolitionStep(syncBaseItem);
        return demolitionStepClips.get(demolitionStep);
    }

    public Map<Integer, Collection<ItemClipPosition>> getDemolitionStepClips() {
        return demolitionStepClips;
    }

	public void setDemolitionStepClips(
			Map<Integer, Collection<ItemClipPosition>> demolitionStepClips) {
		this.demolitionStepClips = demolitionStepClips;
	}
}
