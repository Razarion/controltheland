package com.btxtech.game.jsre.common.gameengine.formation;

import com.btxtech.game.jsre.client.common.Index;

/**
 * User: beat
 * Date: 19.08.2011
 * Time: 12:02:07
 */
public interface Segment {
    static final double DISTANCE = 3.0;

    Index getCross(double angel, Index reference);

    Index getPerpendicular(Index crossPoint, int perpendicularDistance, Index otherDirection);

    //boolean isNextPointOnSegment(PlaceableFormatItem last, PlaceableFormatItem next, Index reference, boolean counterClock);

    //PlaceableFormatItem getNextPoint(PlaceableFormatItem last, PlaceableFormatItem next, Index reference, boolean counterClock);

    Index getNextPoint(boolean counterClock, Index reference, Index crossPoint, double distance);

    boolean isNextPointOnSegment(boolean counterClock, Index reference, Index crossPoint, double distance);

    Index getEndPoint(Index reference, boolean counterClock);
}
