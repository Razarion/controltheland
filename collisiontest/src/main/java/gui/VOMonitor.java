package gui;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.services.collision.CollisionService;
import com.btxtech.game.jsre.common.gameengine.services.collision.OrcaLine;
import com.btxtech.game.jsre.common.gameengine.services.collision.VelocityObstacleManager;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by beat
 * on 20.06.2014.
 */
public class VOMonitor {
    private static final int FRAMES_PER_SECOND = 25;
    private static double ZOOM = 100;
    private static final long TIMER_DELAY = 1000 / FRAMES_PER_SECOND;
    private JFrame frame;
    private JPanel canvas;
    private Timer timer;
    private SyncItem syncItem;
    private CollisionService collisionService;
    private double zoom = ZOOM;

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
        // Setup menu
        JPanel menu = new JPanel();
        menu.setLayout(new FlowLayout());
        frame.getContentPane().add(menu, BorderLayout.PAGE_START);
        // Add canvas
        canvas.setBackground(Color.WHITE);
        frame.getContentPane().add(new JScrollPane(canvas), BorderLayout.CENTER);
        // Zoom spinner
        JSpinner zoomSpinner = new JSpinner();
        zoomSpinner.setValue(zoom);
        menu.add(zoomSpinner);
        zoomSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                try {
                    zoom = ((Number) ((JSpinner) e.getSource()).getValue()).doubleValue();
                    canvas.repaint();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        // Dump button
        JButton dumpButton = new JButton("Dump");
        dumpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("******** DUMP ************");
                VelocityObstacleManager velocityObstacleManager = VOMonitor.this.collisionService.getCaptured();
                if (velocityObstacleManager == null) {
                    return;
                }
                dumpProtagonistMockSyncItem("protagonistMock", velocityObstacleManager.getProtagonist());
                System.out.println("Collection<SyncItem> others = new ArrayList<>();");
                for (OrcaLine orcaLine : velocityObstacleManager.getOrcaLines()) {
                    dumpOtherMockSyncItem("others", orcaLine.getOther());
                }
                System.out.println("VelocityObstacleManager velocityObstacleManager = createVelocityObstacleManager(protagonistMock, others);");
                System.out.println("DecimalPosition optimizedVelocity = velocityObstacleManager.getOptimalVelocity();");
                System.out.println("VelocityManagerVisualizer.startAndWaitForClose(velocityObstacleManager, optimizedVelocity);");
                System.out.println("// TODO assertDecimalPosition(new DecimalPosition(xxx, yyy), optimizedVelocity);");

                System.out.println("******** DUMP END ************");
            }
        });
        menu.add(dumpButton);
        start();
    }

    private void dumpProtagonistMockSyncItem(String variableName, SyncItem syncItem) {
        StringBuilder stringBuilder = new StringBuilder("SyncItem ");
        stringBuilder.append(variableName);
        stringBuilder.append(" = createMockSyncItem(");
        stringBuilder.append(syncItem.getRadius());
        stringBuilder.append(", new DecimalPosition(");
        stringBuilder.append(syncItem.getDecimalPosition().getX());
        stringBuilder.append(", ");
        stringBuilder.append(syncItem.getDecimalPosition().getY());
        stringBuilder.append("), new DecimalPosition(");
        stringBuilder.append(syncItem.getVelocity().getX());
        stringBuilder.append(", ");
        stringBuilder.append(syncItem.getVelocity().getY());
        stringBuilder.append("), new DecimalPosition(");
        stringBuilder.append(syncItem.getTargetPosition().getX());
        stringBuilder.append(", ");
        stringBuilder.append(syncItem.getTargetPosition().getY());
        stringBuilder.append("));");
        System.out.println(stringBuilder.toString());
    }

    private void dumpOtherMockSyncItem(String collectionName, SyncItem syncItem) {
        StringBuilder stringBuilder = new StringBuilder(collectionName);
        stringBuilder.append(".add(createMockSyncItem(");
        stringBuilder.append(syncItem.getRadius());
        stringBuilder.append(", new DecimalPosition(");
        stringBuilder.append(syncItem.getDecimalPosition().getX());
        stringBuilder.append(", ");
        stringBuilder.append(syncItem.getDecimalPosition().getY());
        stringBuilder.append("), new DecimalPosition(");
        stringBuilder.append(syncItem.getVelocity().getX());
        stringBuilder.append(", ");
        stringBuilder.append(syncItem.getVelocity().getY());
        stringBuilder.append("), null));");
        System.out.println(stringBuilder.toString());
    }

    private void render(Graphics graphics) {
        VelocityObstacleManager velocityObstacleManager = collisionService.getCaptured();
        if (velocityObstacleManager == null) {
            return;
        }

        Index middle = new Index((int) (graphics.getClipBounds().getWidth() / 2), (int) (graphics.getClipBounds().getHeight() / 2));
        graphics.setColor(Color.BLACK);
        graphics.drawArc(middle.getX() - 1, middle.getY() - 1, 2, 2, 0, 360);

        for (OrcaLine orcaLine : velocityObstacleManager.getOrcaLines()) {
            drawVelocityObstacle(graphics, orcaLine, Color.RED);

            Index direction = orcaLine.getDirection().multiply(zoom).getPosition().add(middle);
            Index point = orcaLine.getPoint().multiply(zoom).getPosition().add(middle);
            Index lp1 = direction.rotateCounterClock(point, MathHelper.QUARTER_RADIANT);
            Index lp2 = direction.rotateCounterClock(point, MathHelper.THREE_QUARTER_RADIANT);
            Index lp3 = point.rotateCounterClock(lp1, -MathHelper.QUARTER_RADIANT);
            Index lp4 = point.rotateCounterClock(lp2, -MathHelper.THREE_QUARTER_RADIANT);
            int[] xPoints = {lp1.getX(), lp2.getX(), lp4.getX(), lp3.getX()};
            int[] yPoints = {lp1.getY(), lp2.getY(), lp4.getY(), lp3.getY()};
            graphics.setColor(new Color(0f, 0f, 0f, 0.5f));
            graphics.fillPolygon(xPoints, yPoints, xPoints.length);
        }
        // Velocity
        graphics.setColor(Color.BLUE);
        Index velocity = velocityObstacleManager.getProtagonist().getVelocity().multiply(zoom).getPosition().add(middle);
        graphics.drawLine(middle.getX(), middle.getY(), velocity.getX(), velocity.getY());
        // Optimized velocity
        if (velocityObstacleManager.getProtagonist().getOptimizedVelocity() != null) {
            graphics.setColor(Color.MAGENTA);
            Index optimizedVelocity = velocityObstacleManager.getProtagonist().getOptimizedVelocity().multiply(zoom).getPosition().add(middle);
            graphics.drawLine(middle.getX(), middle.getY(), optimizedVelocity.getX(), optimizedVelocity.getY());
        }
    }

    private void drawVelocityObstacle(Graphics graphics, OrcaLine orcaLine, Color red) {
        graphics.setColor(red);

        Index middle = new Index((int) (graphics.getClipBounds().getWidth() / 2), (int) (graphics.getClipBounds().getHeight() / 2));
        Index truncatedCenter = orcaLine.getTruncationMiddle().multiply(zoom).getPosition().add(middle);
        int truncatedRadius = (int) (orcaLine.getTruncationRadius() * zoom);

        graphics.drawArc(truncatedCenter.getX() - truncatedRadius,
                truncatedCenter.getY() - truncatedRadius,
                2 * truncatedRadius,
                2 * truncatedRadius,
                0,
                360);

        Index relativeVelocity = orcaLine.getRelativeVelocity().multiply(zoom).getPosition().add(middle);
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

