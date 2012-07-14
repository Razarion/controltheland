package com.btxtech.game.jsre.client.cockpit.radar;

/**
 * User: beat
 * Date: 13.07.12
 * Time: 08:46
 */
public enum ScaleStep {
    WHOLE_MAP_MISSION(1, true, 1),
    WHOLE_MAP(1, false, 4),
    MORE(4, false, 2),
    DEFAULT(8, false, 1),
    DETAILED(16, true, 1),
    FULL_ZOOM(32, true, 1);

    private final double zoom;
    private final boolean drawImages;
    private final int tileIncrease;

    ScaleStep(double zoom, boolean drawImages, int tileIncrease) {
        this.zoom = zoom;
        this.drawImages = drawImages;
        this.tileIncrease = tileIncrease;
    }

    public double getZoom() {
        return zoom;
    }

    public boolean isDrawImages() {
        return drawImages;
    }

    public int getTileIncrease() {
        return tileIncrease;
    }

    public static ScaleStep zoomIn(ScaleStep current) {
        if(current == WHOLE_MAP_MISSION) {
            return null;
        }

        if (current.ordinal() >= values().length) {
            return null;
        }
        return values()[current.ordinal() + 1];
    }

    public static ScaleStep zoomOut(ScaleStep current) {
        if(current == WHOLE_MAP_MISSION) {
            return null;
        }

        if (current.ordinal() <= WHOLE_MAP.ordinal()) {
            return null;
        }
        return values()[current.ordinal() - 1];
    }

}
