package com.btxtech.game.jsre.common.gameengine.services.collision;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Line;
import com.btxtech.game.jsre.client.common.Rectangle;

import java.io.Serializable;

/**
 * User: beat
 * Date: 14.09.2011
 * Time: 13:26:41
 */
public class Port implements Serializable {
    private Line currentCrossLine;
    private Line destinationCrossLine;
    private Rectangle absoluteCurrent;
    private Rectangle absoluteDestination;

    /**
     * Used by GWT
     */
    Port() {

    }

    public Port(Rectangle absoluteCurrent, Rectangle absoluteDestination) {
        this.absoluteCurrent = absoluteCurrent;
        this.absoluteDestination = absoluteDestination;
        Rectangle crossSection = absoluteCurrent.getCrossSection(absoluteDestination);
        if (absoluteCurrent.containsExclusive(crossSection.getStart())) {
            currentCrossLine = createCrossLine(crossSection);
            destinationCrossLine = createOtherCrossLine(crossSection);
        } else {
            currentCrossLine = createOtherCrossLine(crossSection);
            destinationCrossLine = createCrossLine(crossSection);
        }
    }

    private Line createCrossLine(Rectangle crossSection) {
        if (crossSection.getWidth() == 0 && crossSection.getHeight() > 1) {
            return new Line(crossSection.getStart(), crossSection.getStart().add(0, crossSection.getHeight() - 1));
        } else if (crossSection.getWidth() > 0 && crossSection.getHeight() == 0) {
            return new Line(crossSection.getStart(), crossSection.getStart().add(crossSection.getWidth() - 1, 0));
        } else {
            throw new IllegalArgumentException("Illegal rectangle for cross section: " + crossSection);
        }
    }

    private Line createOtherCrossLine(Rectangle crossSection) {
        if (crossSection.getWidth() == 0 && crossSection.getHeight() > 1) {
            Index start = crossSection.getStart().sub(1, 0);
            return new Line(start, start.add(0, crossSection.getHeight() - 1));
        } else if (crossSection.getWidth() > 0 && crossSection.getHeight() == 0) {
            Index start = crossSection.getStart().sub(0, 1);
            return new Line(start, start.add(crossSection.getWidth() - 1, 0));
        } else {
            throw new IllegalArgumentException("Illegal rectangle for cross section: " + crossSection);
        }
    }

    public Line getCurrentCrossLine() {
        return currentCrossLine;
    }

    public Line getDestinationCrossLine() {
        return destinationCrossLine;
    }

    public Rectangle getAbsoluteCurrent() {
        return absoluteCurrent;
    }

    public Rectangle getAbsoluteDestination() {
        return absoluteDestination;
    }

    public Index getCurrentNearestCrossPoint(Index point) {
        return currentCrossLine.getNearestPointOnLine(point);
    }

    public Index getDestinationNearestCrossPoint(Index point) {
        return destinationCrossLine.getNearestPointOnLine(point);
    }


}
