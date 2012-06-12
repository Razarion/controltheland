package com.btxtech.game.jsre.common.gameengine.syncObjects.command;

import com.btxtech.game.jsre.common.gameengine.services.collision.Path;

/**
 * User: beat
 * Date: 07.10.2011
 * Time: 13:33:30
 */
public abstract class PathToDestinationCommand extends BaseCommand {
    private Path path;

    public Path getPathToDestination() {
        return path;
    }

    public void setPathToDestination(Path path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return super.toString() + " " + path;
    }
}
