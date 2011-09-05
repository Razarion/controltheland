package com.btxtech.game.jsre.client.common;

/**
 * User: beat
 * Date: 19.08.2011
 * Time: 12:02:07
 */
public interface Segment {
    Index getCross(double angel, Index reference);

    boolean isNextPointOnSegment(Index crossPoint, int distance, Index reference, boolean counterClock);

    Index getNextPoint(Index crossPoint, int distance, Index reference, boolean counterClock);

    int getDistanceToEnd(Index crossPoint, Index reference, boolean counterClock, int width);

    Index getPerpendicular(Index crossPoint, int perpendicularDistance, Index otherDirection);

    Index getEndPoint(Index reference, boolean counterClock);
}
