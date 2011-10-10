package com.btxtech.game.jsre.common.gameengine.syncObjects.command;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.CommonJava;

import java.util.List;

/**
 * User: beat
 * Date: 07.10.2011
 * Time: 13:33:30
 */
public abstract class PathToDestinationCommand extends BaseCommand {
    private List<Index> pathToDestination;

    public List<Index> getPathToDestination() {
        return pathToDestination;
    }

    public void setPathToDestination(List<Index> pathToDestination) {
        this.pathToDestination = pathToDestination;
    }

    public Index getDestination() {
        return pathToDestination.get(pathToDestination.size() - 1);
    }

    public boolean hasPathToDestination() {
        return pathToDestination != null && !pathToDestination.isEmpty();
    }

    @Override
    public String toString() {
        return super.toString() + " pathToDestination: " + CommonJava.pathToDestinationAsString(getPathToDestination());
    }
}
