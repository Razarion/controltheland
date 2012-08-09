package com.btxtech.game.jsre.itemtypeeditor;

import java.io.Serializable;

public class ItemTypeImageInfo implements Serializable {
    private int angelIndex;
    private int frame;
    private int step;
    private String base64ImageData;

    /**
     * Used by GWT
     */
    ItemTypeImageInfo() {
    }

    public ItemTypeImageInfo(int angelIndex, int step, int frame, String base64ImageData) {
        this.angelIndex = angelIndex;
        this.step = step;
        this.frame = frame;
        this.base64ImageData = base64ImageData;
    }

    public int getAngelIndex() {
        return angelIndex;
    }

    public int getFrame() {
        return frame;
    }

    public int getStep() {
        return step;
    }

    public String getBase64ImageData() {
        return base64ImageData;
    }


}
