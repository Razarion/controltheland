package com.btxtech.game.jsre.common.gameengine.itemType;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemArea;

import java.io.Serializable;

/**
 * User: beat
 * Date: 17.08.2011
 * Time: 13:00:05
 */
public class BoundingBox implements Serializable {
    private int imageWidth;
    private int imageHeight;
    private int width;
    private int height;
    private int imageCount;

    /**
     * Used by GWT
     */
    private BoundingBox() {
    }

    public BoundingBox(int imageWidth, int imageHeight, int width, int height, int imageCount) {
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.width = width;
        this.height = height;
        this.imageCount = imageCount;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getMaxRadius() {
        return getMaxDiameter() / 2;
    }

    public double getMaxRadiusDouble() {
        return (double) getMaxDiameter() / 2.0;
    }

    public int getMaxDiameter() {
        return (int) Math.round(MathHelper.getPythagoras(width, height));
    }

    public int getMinDiameter() {
        return Math.min(width, height);
    }

    public double getMinRadius() {
        return Math.min(width, height) / 2.0;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public boolean contains(Index middle, Rectangle rectangle) {
        return getRectangle(middle).adjoinsEclusive(rectangle);
    }

    public boolean contains(Index middle, Index point) {
        return getRectangle(middle).containsExclusive(point);
    }

    public Rectangle getRectangle(Index middle) {
        return Rectangle.generateRectangleFromMiddlePoint(middle, width, height);
    }

    public boolean isTurnable() {
        return imageCount > 1;
    }

    public int getImageCount() {
        return imageCount;
    }

    /**
     * The cosmetic image index starts with 1.
     *
     * @return The cosmetic image index starts with 1.
     */
    public int getCosmeticImageIndex() {
        if (isTurnable()) {
            return imageCount / 8;
        } else {
            return 1;
        }
    }

    public double getCosmeticAngel() {
        return (double) getCosmeticImageIndex() / (double) imageCount * MathHelper.ONE_RADIANT;
    }

    public double getAngel(int imageNr) {
        return (double) imageNr * 2.0 * Math.PI / (double) imageCount;
    }

    public int getEffectiveWidth() {
        return width;
    }

    public int getEffectiveHeight() {
        return height;
    }

    public SyncItemArea createSyntheticSyncItemArea(Index destination) {
        SyncItemArea syncItemArea = new SyncItemArea(this, null);
        syncItemArea.setPositionNoCheck(destination);
        return syncItemArea;
    }

    public SyncItemArea createSyntheticSyncItemArea(Index destination, double angel) {
        SyncItemArea syncItemArea = createSyntheticSyncItemArea(destination);
        syncItemArea.setAngel(angel);
        return syncItemArea;
    }

    public int getArea() {
        return width * height;
    }

    public Index getCorner1() {
        return new Index(-width / 2, -height / 2);
    }

    public Index getCorner2() {
        return new Index(-width / 2, height / 2);
    }

    public Index getCorner3() {
        return new Index(width / 2, height / 2);
    }

    public Index getCorner4() {
        return new Index(width / 2, -height / 2);
    }

    public Index getMiddleFromImage() {
        return new Index(imageWidth / 2, imageHeight / 2);
    }

    public Index getMiddleFromImage(Index offset) {
        return getMiddleFromImage().add(offset);
    }

    public int getSmallerstSide() {
        return Math.min(width, height);
    }

    @Override
    public String toString() {
        return "BoundingBox: imageWidth: " + imageWidth + " imageHeight: " + imageHeight + " width: " + width + " height: " + height + " imageCount: " + imageCount;
    }
}
