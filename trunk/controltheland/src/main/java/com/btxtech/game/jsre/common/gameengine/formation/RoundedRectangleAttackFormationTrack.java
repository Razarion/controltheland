package com.btxtech.game.jsre.common.gameengine.formation;

import com.btxtech.game.jsre.client.common.Arc;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Line;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemArea;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: beat
 * Date: 19.08.2011
 * Time: 11:32:28
 */

public class RoundedRectangleAttackFormationTrack {
    private List<Segment> segments = new ArrayList<Segment>();
    private SyncItemArea target;
    private boolean counterClock;
    private Segment crossSegment;
    private Index crossPoint;
    private SyncItemArea last;


    public RoundedRectangleAttackFormationTrack(double startAngel, SyncItemArea target, int range, boolean counterClock) {
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
        crossPoint = setupStartSegment(startAngel);
    }

    public List<Segment> getSegments() {
        return segments;
    }

    public SyncItemArea getStartPoint(AttackFormationItem attackFormationItem) {
        findNextPointOnSegment();
        return createSyncItemArea(attackFormationItem);
    }

    private SyncItemArea createSyncItemArea(AttackFormationItem attackFormationItem) {
        BoundingBox boundingBox = attackFormationItem.getSyncBaseItem().getSyncItemArea().getBoundingBox();
        Index center = crossSegment.getPerpendicular(crossPoint, (int) Math.floor((double) boundingBox.getHeight() / 2.0 - 2.0), target.getPosition());
        SyncItemArea syncItemArea = boundingBox.createSyntheticSyncItemArea(center);
        syncItemArea.turnTo(target);
        return syncItemArea;
    }

    public SyncItemArea getNextPoint(AttackFormationItem attackFormationItem) {
        if (crossSegment == null || last == null) {
            throw new IllegalStateException("Start has not been called before or start failed.");
        }

        int maxTries = 100000;
        while (true) {
            findNextPointOnSegment();
            SyncItemArea syncItemArea = createSyncItemArea(attackFormationItem);
            if (!last.contains(syncItemArea)) {
                last = syncItemArea;
                break;
            }
            maxTries--;
            if (maxTries < 0) {
                throw new IllegalStateException("Max tries in RoundedRectangleAttackFormationTrack exceeded");
            }
        }
        return last;
    }

    public SyncItemArea getLast() {
        return last;
    }

    public void setLast(SyncItemArea last) {
        this.last = last;
    }

    private void findNextPointOnSegment() {
        if (crossSegment.isNextPointOnSegment(counterClock, target.getPosition(), crossPoint, 1)) {
            crossPoint = crossSegment.getNextPoint(counterClock, target.getPosition(), crossPoint, 1);
        } else {
            crossSegment = getNextSegment(crossSegment);
            crossPoint = crossSegment.getEndPoint(target.getPosition(), !counterClock);
        }
    }


    private Segment getNextSegment(Segment crossSegment) {
        int index = segments.indexOf(crossSegment);
        if (index < 0) {
            throw new IllegalStateException("Segment does not exist");
        }
        index++;
        if (index >= segments.size()) {
            index = 0;
        }
        return segments.get(index);
    }

    private Index setupStartSegment(double angel) {
        for (Segment segment : segments) {
            Index point = segment.getCross(angel, target.getPosition());
            if (point != null) {
                crossSegment = segment;
                return point;
            }
        }
        throw new IllegalArgumentException("Start segment can not be found");
    }
}
