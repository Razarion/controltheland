package com.btxtech.game.jsre.common.gameengine.services.collision;

import com.btxtech.game.jsre.client.common.DecimalPosition;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by beat
 * on 09.06.2014.
 * on 09.06.2014.
 */
public class VelocityObstacleManager {
    public static final double FORECAST_FACTOR = 120;
    // public static final double FORECAST_FACTOR = 2;
    private List<OrcaLine> orcaLines = new ArrayList<>();
    private SyncItem protagonist;

    public VelocityObstacleManager(SyncItem protagonist) {
        this.protagonist = protagonist;
    }

    public void inspect(SyncItem other) {
        if (other == protagonist) {
            return;
        }
        double distance = other.getDecimalPosition().getDistance(protagonist.getDecimalPosition()) - other.getRadius() - protagonist.getRadius();
        if (distance > CollisionService.MAX_DISTANCE) {
            return;
        }
        OrcaLine orcaLine = new OrcaLine(protagonist, other, distance);
        orcaLines.add(orcaLine);
    }


    public DecimalPosition getOptimalVelocity() throws NoPreferredVelocityFoundException {
        DecimalPosition preferredVelocity = protagonist.getTargetPosition().sub(protagonist.getDecimalPosition()).normalize(SyncItem.SPEED);

        if (!isViolated(preferredVelocity)) {
            return preferredVelocity;
        }

        DecimalPosition preferredVelocityOnLine = getBestPointOnOrcaLine(preferredVelocity);
        if (preferredVelocityOnLine != null) {
            return preferredVelocityOnLine;
        }

        return getBestPointOnOrcaLineCorner(preferredVelocity);
    }

    private boolean isViolated(DecimalPosition preferredVelocity) {
        if (orcaLines.isEmpty()) {
            return false;
        }
        for (OrcaLine orcaLine : orcaLines) {
            if (orcaLine.isViolated(preferredVelocity)) {
                return true;
            }
        }
        return false;
    }

    private DecimalPosition getBestPointOnOrcaLine(final DecimalPosition preferredVelocity) {
        List<DecimalPosition> pointOnLines = new ArrayList<>();

        for (OrcaLine orcaLine : orcaLines) {
            DecimalPosition point = orcaLine.getPointOnLine(preferredVelocity, true);
            if (point != null && !isViolated(point)) {
                pointOnLines.add(point);

            }
            point = orcaLine.getPointOnLine(preferredVelocity, false);
            if (point != null && !isViolated(point)) {
                pointOnLines.add(point);
            }
        }
        if (pointOnLines.isEmpty()) {
            return null;
        }
        Collections.sort(pointOnLines, new Comparator<DecimalPosition>() {
            @Override
            public int compare(DecimalPosition o1, DecimalPosition o2) {
                return Double.compare(preferredVelocity.sub(o1).getMagnitude(), preferredVelocity.sub(o2).getMagnitude());
            }
        });
        return pointOnLines.get(0);
    }

    private DecimalPosition getBestPointOnOrcaLineCorner(DecimalPosition preferredVelocity) throws NoPreferredVelocityFoundException {
        List<DecimalPosition> corners = new ArrayList<>();

        List<OrcaLine> tmp = new ArrayList<>(orcaLines);

        while (!tmp.isEmpty()) {
            OrcaLine orcaLine = tmp.remove(0);
            for (OrcaLine line : tmp) {
                DecimalPosition cross = line.getCrossPoint(orcaLine);
                if (cross == null) {
                    continue;
                }
                if (isViolated(cross)) {
                    continue;
                }
                if (cross.getLength() > preferredVelocity.getLength()) {
                    continue;
                }
                corners.add(cross);
            }
        }

        if (corners.isEmpty()) {
            throw new NoPreferredVelocityFoundException();
        }

        Collections.sort(corners, new Comparator<DecimalPosition>() {
            @Override
            public int compare(DecimalPosition o1, DecimalPosition o2) {
                return Double.compare(o2.getMagnitude(), o1.getMagnitude());
            }
        });


        return corners.get(0);
    }

    public SyncItem getProtagonist() {
        return protagonist;
    }

    public Collection<OrcaLine> getOrcaLines() {
        return orcaLines;
    }

}