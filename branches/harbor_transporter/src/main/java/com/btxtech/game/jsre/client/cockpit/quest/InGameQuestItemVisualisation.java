package com.btxtech.game.jsre.client.cockpit.quest;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.Region;

/**
 * User: beat
 * Date: 23.10.12
 * Time: 19:27
 */
public class InGameQuestItemVisualisation {
    private Index relativeMiddlePosition;
    private String color;

    public InGameQuestItemVisualisation(String color, Index relativeMiddlePosition) {
        this.color = color;
        this.relativeMiddlePosition = relativeMiddlePosition;
    }

    public Index getRelativeMiddlePosition() {
        return relativeMiddlePosition;
    }

    public String getColor() {
        return color;
    }
}
