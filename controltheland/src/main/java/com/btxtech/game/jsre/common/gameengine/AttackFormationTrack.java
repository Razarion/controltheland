package com.btxtech.game.jsre.common.gameengine;

import com.btxtech.game.jsre.client.common.Arc;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Line;
import com.btxtech.game.jsre.client.common.Segment;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemArea;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: beat
 * Date: 19.08.2011
 * Time: 11:32:28
 */

public class AttackFormationTrack {
    private List<Segment> segments = new ArrayList<Segment>();
    private SyncItemArea target;
    private boolean counterClock;
    private Index crossPoint;
    private Segment crossSegment;


    public AttackFormationTrack(SyncItemArea target, int range, boolean counterClock) {
        this.target = target;
        this.counterClock = counterClock;
        // Line 1
        Line line = new Line(target.getCorner4(), target.getCorner1());
        segments.add(line.translate(target.getAngel(), range));
        // Arc 2
        segments.add(new Arc(target.getCorner1().getPointFromAngelToNord(target.getAngel(), range),
                target.getCorner1().getPointFromAngelToNord(target.getAngel() + MathHelper.QUARTER_RADIANT, range),
                target.getCorner1()));
        // Line 3
        line = new Line(target.getCorner1(), target.getCorner2());
        segments.add(line.translate(target.getAngel() + MathHelper.QUARTER_RADIANT, range));
        // Corner 4
        segments.add(new Arc(target.getCorner2().getPointFromAngelToNord(target.getAngel() + MathHelper.QUARTER_RADIANT, range),
                target.getCorner2().getPointFromAngelToNord(target.getAngel() + MathHelper.QUARTER_RADIANT + MathHelper.QUARTER_RADIANT, range),
                target.getCorner2()));
        // Line 5
        line = new Line(target.getCorner2(), target.getCorner3());
        segments.add(line.translate(target.getAngel() + MathHelper.HALF_RADIANT, range));
        // Arc 6
        segments.add(new Arc(target.getCorner3().getPointFromAngelToNord(target.getAngel() + MathHelper.HALF_RADIANT, range),
                target.getCorner3().getPointFromAngelToNord(target.getAngel() + MathHelper.HALF_RADIANT + MathHelper.QUARTER_RADIANT, range),
                target.getCorner3()));
        // Line 7
        line = new Line(target.getCorner3(), target.getCorner4());
        segments.add(line.translate(target.getAngel() + MathHelper.THREE_QUARTER_RADIANT, range));
        // Arc 8
        segments.add(new Arc(target.getCorner4().getPointFromAngelToNord(target.getAngel() + MathHelper.THREE_QUARTER_RADIANT, range),
                target.getCorner4().getPointFromAngelToNord(target.getAngel() + MathHelper.THREE_QUARTER_RADIANT + MathHelper.QUARTER_RADIANT, range),
                target.getCorner4()));

        if (!counterClock) {
            Segment segment1 = segments.remove(0);
            Collections.reverse(segments);
            segments.add(0, segment1);
        }
    }

    public List<Segment> getSegments() {
        return segments;
    }

    public Index start(double angel, int perpendicularDistance) {
        setupStartSegment(angel);
        return crossSegment.getPerpendicular(crossPoint, perpendicularDistance, target.getPosition());
    }

    public Index getNextPoint(int distance, int perpendicularDistance, int width) {
        if (crossPoint == null || crossSegment == null) {
            throw new IllegalStateException("Start has not been called before or start failed.");
        }

        if (crossSegment.isNextPointOnSegment(crossPoint, distance, target.getPosition(), counterClock)) {
            crossPoint = crossSegment.getNextPoint(crossPoint, distance, target.getPosition(), counterClock);
            return crossSegment.getPerpendicular(crossPoint, perpendicularDistance, target.getPosition());
        } else {
            int deltaDistance = crossSegment.getDistanceToEnd(crossPoint, target.getPosition(), counterClock, width);
            int index = segments.indexOf(crossSegment);
            index++;
            if (index >= segments.size()) {
                index = 0;
            }
            crossSegment = segments.get(index);
            crossPoint = crossSegment.getEndPoint(target.getPosition(), !counterClock);
            return getNextPoint(distance - deltaDistance, perpendicularDistance, width);
        }
    }

    private void setupStartSegment(double angel) {
        for (Segment segment : segments) {
            Index point = segment.getCross(angel, target.getPosition());
            if (point != null) {
                crossPoint = point;
                crossSegment = segment;
                return;
            }
        }
        System.out.println("Reference: " + target.getPosition());
        System.out.println("angel: " + angel);
        for (Segment segment : segments) {
            System.out.println(segment);
        }
        throw new IllegalArgumentException("Start segment can not be found");
    }
}
