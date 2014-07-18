package gui;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.services.collision.CollisionService;
import com.btxtech.game.jsre.common.gameengine.services.collision.OrcaLine;
import com.btxtech.game.jsre.common.gameengine.services.collision.VelocityObstacleManager;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by beat
 * on 20.06.2014.
 */
public class VOMonitor {
    private static final int FRAMES_PER_SECOND = 25;
    private static double ZOOM = 100;
    private static int LEG_LENGTH = (int) (200.0 * ZOOM);
    private static final long TIMER_DELAY = 1000 / FRAMES_PER_SECOND;
    private JFrame frame;
    private JPanel canvas;
    private Timer timer;
    private SyncItem syncItem;
    private CollisionService collisionService;

    public VOMonitor(SyncItem syncItem, CollisionService collisionService) {
        this.syncItem = syncItem;
        this.collisionService = collisionService;
        collisionService.captureSyncItem(syncItem);
        frame = new JFrame("Velocity obstacles monitor: " + syncItem.getId());
        frame.setPreferredSize(new Dimension(800, 800));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        canvas = new JPanel() {
            @Override
            public void paint(Graphics graphics) {
                super.paint(graphics);
                render(graphics);
            }
        };
        canvas.setBackground(Color.WHITE);
        frame.getContentPane().add(new JScrollPane(canvas), BorderLayout.CENTER);
        start();
    }

    private void render(Graphics graphics) {
        VelocityObstacleManager velocityObstacleManager = collisionService.getCaptured();
        Index middle = new Index((int) (graphics.getClipBounds().getWidth() / 2), (int) (graphics.getClipBounds().getHeight() / 2));
        graphics.setColor(Color.BLACK);
        graphics.drawArc(middle.getX() - 1, middle.getY() - 1, 2, 2, 0, 360);

        for (OrcaLine orcaLine : velocityObstacleManager.getOrcaLines()) {
            drawVelocityObstacle(graphics, orcaLine, Color.RED);

            Index direction = orcaLine.getDirection().multiply(ZOOM).getPosition().add(middle);
            Index point = orcaLine.getPoint().multiply(ZOOM).getPosition().add(middle);
            Index lp1 = direction.rotateCounterClock(point, MathHelper.QUARTER_RADIANT);
            Index lp2 = direction.rotateCounterClock(point, MathHelper.THREE_QUARTER_RADIANT);
            Index lp3 = point.rotateCounterClock(lp1, -MathHelper.QUARTER_RADIANT);
            Index lp4 = point.rotateCounterClock(lp2, -MathHelper.THREE_QUARTER_RADIANT);
            int[] xPoints = {lp1.getX(), lp2.getX(), lp4.getX(), lp3.getX()};
            int[] yPoints = {lp1.getY(), lp2.getY(), lp4.getY(), lp3.getY()};
            graphics.setColor(new Color(0f, 0f, 0f, 0.5f));
            //graphics.fillPolygon(xPoints, yPoints, xPoints.length);
        }
        // Velocity
        graphics.setColor(Color.BLUE);
        Index velocity = velocityObstacleManager.getProtagonist().getVelocity().multiply(ZOOM).getPosition().add(middle);
        graphics.drawLine(middle.getX(), middle.getY(), velocity.getX(), velocity.getY());
        // Optimized velocity
        if (velocityObstacleManager.getProtagonist().getOptimizedVelocity() != null) {
            graphics.setColor(Color.MAGENTA);
            Index optimizedVelocity = velocityObstacleManager.getProtagonist().getOptimizedVelocity().multiply(ZOOM).getPosition().add(middle);
            graphics.drawLine(middle.getX(), middle.getY(), optimizedVelocity.getX(), optimizedVelocity.getY());
        }
    }

    private void drawVelocityObstacle(Graphics graphics, OrcaLine orcaLine, Color red) {
        graphics.setColor(red);

        Index middle = new Index((int) (graphics.getClipBounds().getWidth() / 2), (int) (graphics.getClipBounds().getHeight() / 2));
        Index truncatedCenter = orcaLine.getTruncationMiddle().multiply(ZOOM).getPosition().add(middle);
        int truncatedRadius = (int) (orcaLine.getTruncationRadius() * ZOOM);

        graphics.drawArc(truncatedCenter.getX() - truncatedRadius,
                truncatedCenter.getY() - truncatedRadius,
                2 * truncatedRadius,
                2 * truncatedRadius,
                0,
                360);

        Index relativeVelocity = orcaLine.getRelativeVelocity().multiply(ZOOM).getPosition().add(middle);
        graphics.drawLine(middle.getX(), middle.getY(), relativeVelocity.getX(), relativeVelocity.getY());

    }

    public void close() {
        frame.dispose();
        timer.cancel();
        collisionService.releaseCaptureSyncItem();
    }

    private void start() {
        timer = new java.util.Timer("GuiTimerTask", true);
        timer.schedule(new GuiTimerTask(), 0, TIMER_DELAY);
    }

    class GuiTimerTask extends TimerTask {
        public void run() {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (canvas != null) {
                        canvas.repaint();
                    }
                }
            });
        }
    }

}

