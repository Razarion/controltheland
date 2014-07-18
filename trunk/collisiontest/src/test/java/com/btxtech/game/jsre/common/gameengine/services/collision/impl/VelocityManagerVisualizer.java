package com.btxtech.game.jsre.common.gameengine.services.collision.impl;

import com.btxtech.game.jsre.client.common.DecimalPosition;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.services.collision.OrcaLine;
import com.btxtech.game.jsre.common.gameengine.services.collision.VelocityObstacleManager;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * Created by beat
 * on 18.07.2014.
 */
public class VelocityManagerVisualizer {
    private static int INIT_WIDTH = 800;
    private static int INIT_HEIGHT = 800;
    private static final int GRID_SIZE = 20;
    private static double ZOOM = 100;
    private int velocityWidth = INIT_WIDTH;
    private int velocityHeight = INIT_HEIGHT;
    private VelocityObstacleManager velocityObstacleManager;
    private DecimalPosition optimizedVelocity;

    public VelocityManagerVisualizer(VelocityObstacleManager velocityObstacleManager, DecimalPosition optimizedVelocity) {
        this.velocityObstacleManager = velocityObstacleManager;
        this.optimizedVelocity = optimizedVelocity;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame velocityFrame = new JFrame("Static ");
                velocityFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                velocityFrame.setPreferredSize(new Dimension(INIT_WIDTH, INIT_HEIGHT));
                addComponentsToVelocityPane(velocityFrame.getContentPane());
                velocityFrame.pack();
                velocityFrame.setLocationRelativeTo(null);
                velocityFrame.setVisible(true);
            }
        });
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void addComponentsToVelocityPane(Container pane) {
        if (!(pane.getLayout() instanceof BorderLayout)) {
            pane.add(new JLabel("Container doesn't use BorderLayout!"));
            return;
        }

        // Setup menu
        JPanel menu = new JPanel();
        menu.setLayout(new FlowLayout());
        pane.add(menu, BorderLayout.PAGE_START);

        final JLabel mouseLabel = new JLabel("Mouse Position:");
        menu.add(mouseLabel);

        // Setup canvas
        JPanel velocityCanvas = new JPanel() {
            @Override
            public void paint(Graphics graphics) {
                velocityWidth = (int) graphics.getClipBounds().getWidth();
                velocityHeight = (int) graphics.getClipBounds().getHeight();
                super.paint(graphics);
                drawGrid(graphics, velocityWidth, velocityHeight);
                try {
                    render(graphics);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        velocityCanvas.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                try {
                    DecimalPosition mouse = new DecimalPosition(e.getX() - velocityWidth / 2, e.getY() - velocityHeight / 2).divide(ZOOM);
                    mouseLabel.setText("Mouse Position: " + mouse.getX() + ":" + mouse.getY());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        velocityCanvas.setBackground(Color.WHITE);
        pane.add(new JScrollPane(velocityCanvas), BorderLayout.CENTER);
    }

    private void drawGrid(Graphics graphics, int width, int height) {
        Index middle = getMiddle(graphics);

        graphics.setColor(Color.LIGHT_GRAY);
        int xOffset = (width / 2) % GRID_SIZE;
        int yOffset = (height / 2) % GRID_SIZE;
        for (int i = xOffset; i < width; i += GRID_SIZE) {
            graphics.drawLine(i, 0, i, height);
        }
        for (int i = yOffset; i < height; i += GRID_SIZE) {
            graphics.drawLine(0, i, width, i);
        }

        graphics.setColor(Color.DARK_GRAY);
        graphics.drawLine(0, middle.getY(), width, middle.getY());
        graphics.drawLine(middle.getX(), 0, middle.getX(), height);
    }

    private Index getMiddle(Graphics graphics) {
        return new Index((int) (graphics.getClipBounds().getWidth() / 2), (int) (graphics.getClipBounds().getHeight() / 2));
    }

    private void render(Graphics graphics) {
        Index middle = new Index((int) (graphics.getClipBounds().getWidth() / 2), (int) (graphics.getClipBounds().getHeight() / 2));
        graphics.setColor(Color.BLACK);
        graphics.drawArc(middle.getX() - 1, middle.getY() - 1, 2, 2, 0, 360);

        for (OrcaLine orcaLine : velocityObstacleManager.getOrcaLines()) {
            drawVelocityObstacle(graphics, orcaLine, Color.RED);

            // Relative other position
            graphics.setColor(Color.RED);
            Index relativePosition = orcaLine.getRelativePosition().multiply(ZOOM).getPosition().add(middle);
            int combinedRadius = (int) (orcaLine.getCombinedRadius() * ZOOM);
            graphics.drawArc(relativePosition.getX() - combinedRadius, relativePosition.getY() - combinedRadius, combinedRadius * 2, combinedRadius * 2, 0, 360);

            // Orca line
            Index direction = orcaLine.getDirection().multiply(ZOOM).getPosition().add(middle);
            Index point = orcaLine.getPoint().multiply(ZOOM).getPosition().add(middle);
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
        Index velocity = velocityObstacleManager.getProtagonist().getVelocity().multiply(ZOOM).getPosition().add(middle);
        graphics.drawLine(middle.getX(), middle.getY(), velocity.getX(), velocity.getY());
        // Optimized velocity
        if (optimizedVelocity != null) {
            graphics.setColor(Color.MAGENTA);
            Index relativeOptimizedVelocity = optimizedVelocity.multiply(ZOOM).getPosition().add(middle);
            graphics.drawLine(middle.getX(), middle.getY(), relativeOptimizedVelocity.getX(), relativeOptimizedVelocity.getY());
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

}
