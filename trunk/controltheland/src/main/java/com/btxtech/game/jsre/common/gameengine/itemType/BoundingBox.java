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
    private int width;
    private int height;
    private double[] angels;
    private int maxDiameter;
    private int cosmeticAngelIndex;
    private double cosmeticAngel;

    /**
     * Used by GWT
     */
    protected BoundingBox() {
    }

    public BoundingBox(int width, int height, double[] angels) {
        this.width = width;
        this.height = height;
        setupMaxDiameter();
        this.angels = angels;
        if (angels.length == 0) {
            cosmeticAngelIndex = 0;
            cosmeticAngel = 0;
        } else {
            cosmeticAngelIndex = angels.length / 8;
            cosmeticAngel = angels[cosmeticAngelIndex];
        }
    }

    public void setWidth(int width) {
        this.width = width;
        setupMaxDiameter();
    }

    public void setHeight(int height) {
        this.height = height;
        setupMaxDiameter();
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
        return maxDiameter;
    }

    private void setupMaxDiameter() {
        maxDiameter = (int) Math.round(MathHelper.getPythagoras(width, height));
    }

    public int getMinDiameter() {
        return Math.min(width, height);
    }

    public double getMinRadius() {
        return Math.min(width, height) / 2.0;
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

    public double getCosmeticAngel() {
        return cosmeticAngel;
    }

    public int getCosmeticAngelIndex() {
        return cosmeticAngelIndex;
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

    public int getSmallerstSide() {
        return Math.min(width, height);
    }

    public double getAllowedAngel(double angel) {
        angel = MathHelper.normaliseAngel(angel);
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
        angel = MathHelper.normaliseAngel(angel);
        exceptThatAngel = MathHelper.normaliseAngel(exceptThatAngel);
        if (MathHelper.compareWithPrecision(angel, exceptThatAngel)) {
            for (int i = 0; i < angels.length; i++) {
                double allowedAngel = angels[i];
                if (MathHelper.compareWithPrecision(allowedAngel, exceptThatAngel)) {
                    double prevAngel = angels[i == 0 ? angels.length - 1 : i - 1];
                    double nextAngel = angels[i == angels.length - 1 ? 0 : i + 1];
                    if (MathHelper.getAngel(angel, prevAngel) < MathHelper.getAngel(angel, nextAngel)) {
                        return prevAngel;
                    } else {
                        return nextAngel;
                    }
                }
            }
            throw new IllegalArgumentException("exceptThatAngel is unknown:" + exceptThatAngel);
        } else {
            for (int i = 0; i < angels.length; i++) {
                double allowedAngel = angels[i];
                if (MathHelper.compareWithPrecision(allowedAngel, exceptThatAngel)) {
                    if (MathHelper.isCounterClock(angel, exceptThatAngel)) {
                        return angels[i == 0 ? angels.length - 1 : i - 1];
                    } else {
                        return angels[i == angels.length - 1 ? 0 : i + 1];
                    }
                }
            }
            throw new IllegalArgumentException("exceptThatAngel is unknown:" + exceptThatAngel);
        }
    }

    public double[] getAngels() {
        return angels;
    }

    public void setAngels(double[] angels) {
        this.angels = angels;
    }

    public int getAngelCount() {
        if (angels.length == 0) {
            return 1;
        } else {
            return angels.length;
        }
    }

    /**
     * @param angelIndex 0..x
     * @return allowed angel
     */
    public double angelIndexToAngel(int angelIndex) {
        return angels[angelIndex];
    }

    public int angelToAngelIndex(double angel) {
        if (angels.length == 0) {
            return 0;
        }
        // TODO slow !!! Is called in every render frame
        angel = getAllowedAngel(angel);
        for (int i = 0; i < angels.length; i++) {
            double allowedAngel = angels[i];
            if (MathHelper.compareWithPrecision(allowedAngel, angel)) {
                return i;
            }

        }
        throw new IllegalArgumentException("angelToImageNr angel is unknown:" + angel);
    }


    @Override
    public String toString() {
        return "BoundingBox: width: " + width + " height: " + height + " angels: " + angels.length;
    }
}
