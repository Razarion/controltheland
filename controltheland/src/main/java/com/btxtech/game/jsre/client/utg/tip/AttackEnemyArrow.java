package com.btxtech.game.jsre.client.utg.tip;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Line;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.utg.ClientUserTracker;
import com.btxtech.game.jsre.common.Html5NotSupportedException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;

import java.util.List;

/**
 * User: beat
 * Date: 19.12.2011
 * Time: 18:07:23
 */
public class AttackEnemyArrow {
    private static final int ARROW_LENGTH = 200;
    private static final int ARROW_THICKNESS = 150;
    private Canvas canvas;
    private double angel;
    private Rectangle arrowCanvasRectangle;
    private boolean visible = true;

    public AttackEnemyArrow(SyncBaseItem enemyBaseItem) {
        canvas = Canvas.createIfSupported();
        if (canvas == null) {
            throw new Html5NotSupportedException("No Canvas for AttackEnemyArrow");
        }
        Rectangle rectangle = TerrainView.getInstance().getViewRect();
        Line line = new Line(rectangle.getCenter(), enemyBaseItem.getSyncItemArea().getPosition());
        List<Index> crossPoints = rectangle.getCrossPointsExclusive(line);
        Index arrowHead;
        if (crossPoints.size() == 1) {
            arrowHead = crossPoints.get(0);
        } else if (crossPoints.size() == 0) {
            arrowHead = enemyBaseItem.getSyncItemArea().getPosition();
        } else {
            throw new IllegalArgumentException("AttackEnemyArrow can not calculate arrow head point");
        }
        if (rectangle.getCenter().equals(arrowHead)) {
            angel = 0;
        } else {
            angel = rectangle.getCenter().getAngleToNord(arrowHead);
        }

        arrowCanvasRectangle = new Rectangle((TerrainView.getInstance().getViewWidth() - ARROW_THICKNESS) / 2,
                TerrainView.getInstance().getViewHeight() / 2 - ARROW_LENGTH,
                ARROW_THICKNESS,
                ARROW_LENGTH);
        arrowCanvasRectangle = arrowCanvasRectangle.getSurroundedRectangle(new Index(TerrainView.getInstance().getViewWidth() / 2, TerrainView.getInstance().getViewHeight() / 2), angel);
        arrowHead = TerrainView.getInstance().toRelativeIndex(arrowHead);
        crossPoints = arrowCanvasRectangle.getCrossPointsExclusive(new Line(arrowCanvasRectangle.getCenter(), arrowHead));
        Index diffPoint;
        if (crossPoints.size() == 1) {
            diffPoint = arrowHead.sub(crossPoints.get(0));
        } else if (crossPoints.size() == 0) {
            crossPoints = arrowCanvasRectangle.getCrossPointsExclusive(new Line(arrowCanvasRectangle.getCenter(), angel, 10000));
            if (crossPoints.size() != 1) {
                throw new IllegalArgumentException("AttackEnemyArrow can not calculate rectangle shift diff point (2). " + crossPoints.size() + " " + arrowCanvasRectangle + " " + new Line(arrowCanvasRectangle.getCenter(), arrowHead));
            }
            diffPoint = arrowHead.sub(crossPoints.get(0));
        } else {
            throw new IllegalArgumentException("AttackEnemyArrow can not calculate rectangle shift diff point. " + crossPoints.size() + " " + arrowCanvasRectangle + " " + new Line(arrowCanvasRectangle.getCenter(), arrowHead));
        }
        arrowCanvasRectangle.shift(diffPoint);

        MapWindow.getAbsolutePanel().add(canvas, arrowCanvasRectangle.getX(), arrowCanvasRectangle.getY());
        canvas.getElement().getStyle().setZIndex(Constants.Z_INDEX_TIP);
        canvas.setCoordinateSpaceWidth(arrowCanvasRectangle.getWidth());
        canvas.setCoordinateSpaceHeight(arrowCanvasRectangle.getHeight());
        drawArrow();


        ClientUserTracker.getInstance().onDialogAppears(canvas, "Tip Arrow");
    }

    private void drawArrow() {
        Context2d context2d = canvas.getContext2d();
        context2d.save();
        context2d.translate(arrowCanvasRectangle.getWidth() / 2, arrowCanvasRectangle.getHeight() / 2);
        context2d.rotate(-angel);
        context2d.setStrokeStyle("#FF0000");
        context2d.setFillStyle("#FF0000");
        context2d.beginPath();
        context2d.moveTo(0, -ARROW_LENGTH / 2);
        context2d.lineTo(ARROW_THICKNESS / 2 - 1, -10);
        context2d.lineTo(30, -10);
        context2d.lineTo(30, ARROW_LENGTH / 2 - 1);
        context2d.lineTo(-30, ARROW_LENGTH / 2 - 1);
        context2d.lineTo(-30, -10);
        context2d.lineTo(-ARROW_THICKNESS / 2, -10);
        context2d.closePath();
        context2d.fill();
        context2d.stroke();
        context2d.restore();
    }

    public void blink() {
        if (visible) {
            canvas.getContext2d().clearRect(0, 0, arrowCanvasRectangle.getWidth(), arrowCanvasRectangle.getHeight());
        } else {
            drawArrow();
        }
        visible = !visible;
    }

    public void close() {
        ClientUserTracker.getInstance().onDialogDisappears(canvas);
        MapWindow.getAbsolutePanel().remove(canvas);
    }
}
