package com.btxtech.game.jsre.common.gameengine.services.collision;

import com.btxtech.game.jsre.client.common.DecimalPosition;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

import javax.swing.plaf.synth.SynthUI;
import javax.xml.ws.Holder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by beat
 * on 09.06.2014.
 */
public class VelocityObstacleManager {
    public static final double FORECAST_FACTOR = 120;
   // public static final double FORECAST_FACTOR = 2;
    private static final double AVO_EPSILON = 1.0e-6;
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
        if (distance < 0.0) {
            System.out.println("C*R*A*S*H"); // TODO
            return;
        }
        orcaLines.add(new OrcaLine(protagonist, other));
    }


    public DecimalPosition getOptimalVelocity() {
        DecimalPosition preferredVelocity = protagonist.getTargetPosition().sub(protagonist.getDecimalPosition()).normalize(SyncItem.SPEED);
        if(orcaLines.isEmpty()) {
            return preferredVelocity;
        } else {
            if(orcaLines.get(0).isHasViolation()) {
                return orcaLines.get(0).nearestVelocity(preferredVelocity);
            } else {
                return preferredVelocity;
            }
        }


        // old
/*        Holder<DecimalPosition> velocityHolder = new Holder<>(new DecimalPosition(0, 0));
        int lineFail = linearProgram2(orcaLines, SyncItem.SPEED, protagonist.getPreferredVelocity(1.0), false, velocityHolder);

        if (lineFail < orcaLines.size()) {
            linearProgram3(orcaLines, 0, lineFail, SyncItem.SPEED, velocityHolder);
        }
        return velocityHolder.value;*/
    }

    public SyncItem getProtagonist() {
        return protagonist;
    }

    public Collection<OrcaLine> getOrcaLines() {
        return orcaLines;
    }

    private int linearProgram2(List<OrcaLine> lines, double speed, DecimalPosition optVelocity, boolean directionOpt, Holder<DecimalPosition> result) {
        if (directionOpt) {
            // Optimize direction. Note that the optimization velocity is of unit length in this case.
            result.value = optVelocity.multiply(speed);
        } else if (optVelocity.getLength() > speed) {
            // Optimize closest point and outside circle.
            result.value = optVelocity.normalize(speed);
        } else {
            // Optimize closest point and inside circle.
            result.value = optVelocity;
        }

        int i = 0;
        for (OrcaLine line : lines) {
            if (line.getDirection().determinant(line.getPoint().sub(result.value)) > 0.0) {
                // Result does not satisfy constraint i. Compute new optimal result.
                DecimalPosition tempResult = new DecimalPosition(result.value);
                if (!linearProgram1(lines, i, speed, optVelocity, directionOpt, result)) {
                    result.value = tempResult;
                    return i;
                }
            }
            i++;
        }

        return lines.size();
    }

    private boolean linearProgram1(List<OrcaLine> lines, int lineNo, double speed, DecimalPosition optVelocity, boolean directionOpt, Holder<DecimalPosition> result) {
        double dotProduct = lines.get(lineNo).getPoint().dotProduct(lines.get(lineNo).getDirection());
        double discriminant = Math.pow(dotProduct, 2) + Math.pow(speed, 2) - Math.pow(lines.get(lineNo).getPoint().getLength(), 2);

        if (discriminant < 0.0f) {
            /* Max speed circle fully invalidates line lineNo. */
            return false;
        }

        double sqrtDiscriminant = Math.sqrt(discriminant);
        double tLeft = -dotProduct - sqrtDiscriminant;
        double tRight = -dotProduct + sqrtDiscriminant;

        for (int i = 0; i < lineNo; ++i) {
            double denominator = lines.get(lineNo).getDirection().determinant(lines.get(i).getDirection());
            double numerator = lines.get(i).getDirection().determinant(lines.get(lineNo).getPoint().sub(lines.get(i).getPoint()));

            if (Math.abs(denominator) <= AVO_EPSILON) {
                /* Lines lineNo and i are (almost) parallel. */
                if (numerator < 0.0f) {
                    return false;
                } else {
                    continue;
                }
            }

            double t = numerator / denominator;

            if (denominator >= 0.0f) {
                /* Line i bounds line lineNo on the right. */
                tRight = Math.min(tRight, t);
            } else {
                /* Line i bounds line lineNo on the left. */
                tLeft = Math.max(tLeft, t);
            }

            if (tLeft > tRight) {
                return false;
            }
        }

        if (directionOpt) {
            /* Optimize direction. */
            if (optVelocity.dotProduct(lines.get(lineNo).getDirection()) > 0.0) {
                /* Take right extreme. */
                result.value = lines.get(lineNo).getPoint().add(lines.get(lineNo).getDirection().multiply(tRight));
            } else {
                /* Take left extreme. */
                result.value = lines.get(lineNo).getPoint().add(lines.get(lineNo).getDirection().multiply(tLeft));
            }
        } else {
            /* Optimize closest point. */
            double t = lines.get(lineNo).getDirection().dotProduct(optVelocity.sub(lines.get(lineNo).getPoint()));

            if (t < tLeft) {
                result.value = lines.get(lineNo).getPoint().add(lines.get(lineNo).getDirection().multiply(tLeft));
            } else if (t > tRight) {
                result.value = lines.get(lineNo).getPoint().add(lines.get(lineNo).getDirection().multiply(tRight));
            } else {
                result.value = lines.get(lineNo).getPoint().add(lines.get(lineNo).getDirection().multiply(t));
            }
        }

        return true;
    }

    private void linearProgram3(List<OrcaLine> lines, int numObstLines, int beginLine, double radius, Holder<DecimalPosition> result) {
        double distance = 0.0f;

        for (int i = beginLine; i < lines.size(); ++i) {
            if (lines.get(i).getDirection().determinant(lines.get(i).getPoint().sub(result.value)) > distance) {
                /* Result does not satisfy constraint of line i. */
                //std::vector<Line> projLines(lines.begin(), lines.begin() + numObstLines);
                List<OrcaLine> projLines = new ArrayList<>();
                for (int ii = 0; ii < numObstLines; ++ii) {
                    projLines.add(lines.get(ii));
                }

                for (int j = numObstLines; j < i; ++j) {
                    OrcaLine line = new OrcaLine();

                    double determinant = lines.get(i).getDirection().determinant(lines.get(j).getDirection());

                    if (Math.abs(determinant) <= AVO_EPSILON) {
                        /* Line i and line j are parallel. */
                        if (lines.get(i).getDirection().dotProduct(lines.get(j).getDirection()) > 0.0) {
                            /* Line i and line j point in the same direction. */
                            continue;
                        } else {
                            /* Line i and line j point in opposite direction. */
                            line.setPoint(lines.get(i).getPoint().add(lines.get(j).getPoint()).multiply(0.5));
                        }
                    } else {
                        line.setPoint(lines.get(i).getPoint().add(
                                lines.get(i).getDirection().multiply(
                                        lines.get(j).getDirection().determinant(lines.get(i).getPoint().sub(lines.get(j).getPoint())) / determinant)));


                        line.setDirection(lines.get(j).getDirection().sub(lines.get(i).getDirection()).normalize());
                        projLines.add(line);
                    }

                    DecimalPosition tempResult = new DecimalPosition(result.value);
                    if (linearProgram2(projLines, radius, new DecimalPosition(-lines.get(i).getDirection().getY(), lines.get(i).getDirection().getY()), true, result) < projLines.size()) {
                    /* This should in principle not happen.  The result is by definition
                     * already in the feasible region of this linear program. If it fails,
                     * it is due to small floating point error, and the current result is
                     * kept.
                     */
                        result.value = tempResult;
                    }

                    distance = lines.get(i).getDirection().determinant(lines.get(i).getPoint().sub(new DecimalPosition(result.value)));
                }
            }
        }

    }
}