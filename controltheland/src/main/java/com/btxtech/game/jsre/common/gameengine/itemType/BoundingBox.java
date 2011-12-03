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
    private double[] angels;

    /**
     * Used by GWT
     */
    protected BoundingBox() {
    }

    public BoundingBox(int imageWidth, int imageHeight, int width, int height, double[] angels) {
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.width = width;
        this.height = height;
        this.angels = angels;
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
        return angels.length > 1;
    }

    /**
     * The cosmetic image index starts with 1.
     *
     * @return The cosmetic image index starts with 1.
     */
    public int getCosmeticImageIndex() {
        if (isTurnable()) {
            return angels.length / 8;
        } else {
            return 1;
        }
    }

    public double getCosmeticAngel() {
        return (double) getCosmeticImageIndex() / (double) angels.length * MathHelper.ONE_RADIANT;
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

    public Index getTopLeftFromImage(Index offset) {
        return offset.sub(getMiddleFromImage());
    }

    public int getSmallerstSide() {
        return Math.min(width, height);
    }

    public double getAllowedAngel(double angel) {
        double angel1 = angels[0];
        for (double angel2 : angels) {
            double result = MathHelper.closerToAngel(angel, angel1, angel2);
            if (angel2 == result) {
                angel1 = angel2;
            }
        }
        return angel1;
    }

    public double getAllowedAngel(double angel, double exceptThatAngel) {
        double angel1 = MathHelper.compareWithPrecision(angels[0], exceptThatAngel) ? angels[1] : angels[0];
        for (double angel2 : angels) {
            if (MathHelper.compareWithPrecision(angel2, exceptThatAngel)) {
                continue;
            }
            double result = MathHelper.closerToAngel(angel, angel1, angel2);
            if (angel2 == result) {
                angel1 = angel2;
            }
        }
        return angel1;
    }

    public double[] getAngels() {
        return angels;
    }

    /**
     * @param imageNr 0..x
     * @return allowed angel
     */
    public double imageNumberToAngel(int imageNr) {
        return angels[imageNr];
    }

    public int angelToImageNr(double angel) {
        angel = MathHelper.normaliseAngel(angel);
        double minDelta = Double.MAX_VALUE;
        int imageNr = 0;
        for (int i = 0, angelsLength = angels.length; i < angelsLength; i++) {
            double allowedAngel = angels[i];
            double delta = Math.abs(allowedAngel - angel);
            if (delta < minDelta) {
                minDelta = delta;
                imageNr = i;
            }
        }
        return imageNr;
    }

    @Override
    public String toString() {
        return "BoundingBox: imageWidth: " + imageWidth + " imageHeight: " + imageHeight + " width: " + width + " height: " + height + " angels: " + angels.length;
    }
}
