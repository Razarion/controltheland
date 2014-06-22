package gui;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.services.collision.CollisionService;
import com.btxtech.game.jsre.common.gameengine.services.collision.VelocityObstacle;
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
    private static double ZOOM = 3.0;
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
        graphics.setColor(Color.BLUE);

        graphics.drawArc(middle.getX() - 1,
                middle.getY() - 1,
                2,
                2,
                0, 360);

        Index velocity = velocityObstacleManager.getProtagonist().getVelocity(VelocityObstacleManager.FORECAST_FACTOR).multiply(ZOOM).getPosition().add(middle);
        graphics.drawLine(middle.getX(), middle.getY(), velocity.getX(), velocity.getY());

        for (VelocityObstacle velocityObstacle : velocityObstacleManager.getVelocityObstacles()) {
            if (velocityObstacleManager.isInside(velocityObstacle)) {
                graphics.setColor(new Color(1.0f, 0.0f, 0.0f, 0.5f));
            } else {
                graphics.setColor(new Color(0.2f, 0.2f, 0.2f, 0.5f));
            }

            Index apex = velocityObstacle.getApex().multiply(ZOOM).getPosition().add(middle);

            /*graphics.drawArc(apex.getX() - 1,
                    apex.getY() - 1,
                    2,
                    2,
                    0, 360);*/
            Index leg1 = apex.getPointFromAngelToNord(velocityObstacle.getStartAngel(), LEG_LENGTH);
            //graphics.drawLine(apex.getX(), apex.getY(), leg1.getX(), leg1.getY());
            Index leg2 = apex.getPointFromAngelToNord(velocityObstacle.getEndAngel(), LEG_LENGTH);
            //graphics.drawLine(apex.getX(), apex.getY(), leg2.getX(), leg2.getY());
            //graphics.drawLine(leg1.getX(), leg1.getY(), leg2.getX(), leg2.getY());

            graphics.fillPolygon(new int[]{apex.getX(), leg1.getX(), leg2.getX()}, new int[]{apex.getY(), leg1.getY(), leg2.getY()}, 3);

        }


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

