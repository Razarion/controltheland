package com.btxtech.game.jsre.common.gameengine.syncObjects.command;

import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;

/**
 * User: beat
 * Date: 21.05.12
 * Time: 00:27
 */
public class PickupBoxCommand extends PathToDestinationCommand {
    private Id box;

    public Id getBox() {
        return box;
    }

    public void setBox(Id box) {
        this.box = box;
    }
}
