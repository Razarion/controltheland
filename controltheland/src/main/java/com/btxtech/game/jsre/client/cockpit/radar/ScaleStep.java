package com.btxtech.game.jsre.client.cockpit.radar;

/**
 * User: beat
 * Date: 13.07.12
 * Time: 08:46
 */
public enum ScaleStep {
    WHOLE_MAP_MISSION(1),
    WHOLE_MAP(1),
    MORE(4),
    DEFAULT(8),
    DETAILED(16),
    FULL_ZOOM(32);

    private final double zoom;

    ScaleStep(double zoom) {
        this.zoom = zoom;
    }

    public double getZoom() {
        return zoom;
    }

    public static ScaleStep zoomIn(ScaleStep current) {
        if (current == WHOLE_MAP_MISSION) {
            return null;
        }

        if (current.ordinal() >= values().length) {
            return null;
        }
        return values()[current.ordinal() + 1];
    }

    public static ScaleStep zoomOut(ScaleStep current) {
        if (current == WHOLE_MAP_MISSION) {
            return null;
        }

        if (current.ordinal() <= WHOLE_MAP.ordinal()) {
            return null;
        }
        return values()[current.ordinal() - 1];
    }

}
