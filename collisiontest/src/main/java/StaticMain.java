import com.btxtech.game.jsre.client.common.DecimalPosition;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.services.collision.NoPreferredVelocityFoundException;
import com.btxtech.game.jsre.common.gameengine.services.collision.OrcaLine;
import com.btxtech.game.jsre.common.gameengine.services.collision.VelocityObstacleManager;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by beat
 * on 26.06.2014.
 */
public class StaticMain {
    private static final int GRID_SIZE = 20;
    private static final int CROSS_SIZE = 5;
    private static int INIT_WIDTH = 800;
    private static int INIT_HEIGHT = 800;
    private int velocityWidth = INIT_WIDTH;
    private int velocityHeight = INIT_HEIGHT;
    private int placeWidth = INIT_WIDTH;
    private int placeHeight = INIT_HEIGHT;
    private VelocityObstacleManager velocityObstacleManager;
    // private Index middle = new Index(INIT_X / 2, INIT_Y / 2);
    private SyncItem protagonist;
    private Collection<SyncItem> syncItems = new ArrayList<>();
    private DecimalPosition optimizedVelocity;
    private Index target;
    private JPanel velocityCanvas;
    private JPanel placeCanvas;
    private JSpinner xSpinner;
    private JSpinner ySpinner;
    private JSpinner xPrefSpinner;
    private JSpinner yPrefSpinner;

    public StaticMain() {
        setupModel();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame velocityFrame = new JFrame("Static ");
                velocityFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                velocityFrame.setPreferredSize(new Dimension(INIT_WIDTH, INIT_HEIGHT));
                addComponentsToVelocityPane(velocityFrame.getContentPane());
                velocityFrame.pack();
                velocityFrame.setLocationRelativeTo(null);
                velocityFrame.setVisible(true);

                JFrame placeFrame = new JFrame("Static ");
                placeFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                placeFrame.setPreferredSize(new Dimension(INIT_WIDTH, INIT_HEIGHT));
                addComponentsToPlacePane(placeFrame.getContentPane());
                placeFrame.pack();
                placeFrame.setLocationRelativeTo(null);
                placeFrame.setVisible(true);
            }
        });
    }

    private void setupModel() {
        protagonist = new SyncItem(10, new Index(0, 0));
        protagonist.setVelocity(new DecimalPosition(-30, -31).normalize(SyncItem.SPEED));
        SyncItem syncItem = new SyncItem(10, new Index(-20, 0));
        syncItem.setVelocity(new DecimalPosition(30, 30).normalize(SyncItem.SPEED));
        syncItems.add(syncItem);
        // syncItem = new SyncItem(10, new Index(-20, -90));
        // syncItem.setVelocity(new DecimalPosition(0, 40).normalize(SyncItem.SPEED));
        // syncItems.add(syncItem);
        //syncItem = new SyncItem(10, new Index(30, -50));
        //syncItem.setVelocity(new DecimalPosition(0, 40).normalize(SyncItem.SPEED));
        //syncItems.add(syncItem);
        target = new Index(-100, -100);

        updateModel();
    }

    private void updateModel() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    velocityObstacleManager = new VelocityObstacleManager(protagonist);
                    protagonist.setTarget(target);
                    for (SyncItem syncItem : syncItems) {
                        velocityObstacleManager.inspect(syncItem);
                    }
                    try {
                        optimizedVelocity = velocityObstacleManager.getOptimalVelocity();
                    } catch (NoPreferredVelocityFoundException e) {
                        e.printStackTrace();
                    }
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            repaint();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        // System.out.println("protagonist: " + protagonist);
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
        velocityCanvas = new JPanel() {
            @Override
            public void paint(Graphics graphics) {
                velocityWidth = (int) graphics.getClipBounds().getWidth();
                velocityHeight = (int) graphics.getClipBounds().getHeight();
                super.paint(graphics);
                drawGrid(graphics, velocityWidth, velocityHeight);
                try {
                    customDrawVelocity(graphics, velocityWidth, velocityHeight);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        JSpinner xPosSpinner = new JSpinner();
        xPosSpinner.setValue(protagonist.getDecimalPosition().getX());
        menu.add(xPosSpinner);
        xPosSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                try {
                    double newX = ((Number) ((JSpinner) e.getSource()).getValue()).doubleValue();
                    protagonist.setDecimalPosition(new DecimalPosition(newX, protagonist.getDecimalPosition().getY()));
                    updateModel();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        JSpinner yPosSpinner = new JSpinner();
        yPosSpinner.setValue(protagonist.getDecimalPosition().getY());
        menu.add(yPosSpinner);
        yPosSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                try {
                    double newY = ((Number) ((JSpinner) e.getSource()).getValue()).doubleValue();
                    protagonist.setDecimalPosition(new DecimalPosition(protagonist.getDecimalPosition().getX(), newY));
                    updateModel();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        xSpinner = new JSpinner();
        xSpinner.setValue(protagonist.getVelocity().getX());
        menu.add(xSpinner);
        xSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                try {
                    double newX = ((Number) ((JSpinner) e.getSource()).getValue()).doubleValue();
                    protagonist.setVelocity(new DecimalPosition(newX, protagonist.getVelocity().getY())/*.normalize(SyncItem.SPEED)*/);
                    updateModel();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        ySpinner = new JSpinner();
        ySpinner.setValue(protagonist.getVelocity().getY());
        menu.add(ySpinner);
        ySpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                try {
                    double newY = ((Number) ((JSpinner) e.getSource()).getValue()).doubleValue();
                    protagonist.setVelocity(new DecimalPosition(protagonist.getVelocity().getX(), newY)/*.normalize(SyncItem.SPEED)*/);
                    updateModel();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        velocityCanvas.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                try {
                    Index mouse = new Index(e.getX() - velocityWidth / 2, velocityHeight / 2 - e.getY());
                    mouseLabel.setText("Mouse Position: " + mouse.getX() + ":" + mouse.getY());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        xPrefSpinner = new JSpinner();
        xPrefSpinner.setValue(target.getX());
        menu.add(xPrefSpinner);
        xPrefSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                try {
                    int newX = (int) ((JSpinner) e.getSource()).getValue();
                    target = new Index(newX, target.getY());
                    updateModel();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        yPrefSpinner = new JSpinner();
        yPrefSpinner.setValue(target.getY());
        menu.add(yPrefSpinner);
        yPrefSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                try {
                    int newY = (int) ((JSpinner) e.getSource()).getValue();
                    target = new Index(target.getX(), newY);
                    updateModel();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        velocityCanvas.setBackground(Color.WHITE);
        pane.add(new JScrollPane(velocityCanvas), BorderLayout.CENTER);
    }


    private void addComponentsToPlacePane(Container pane) {
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
        placeCanvas = new JPanel() {
            @Override
            public void paint(Graphics graphics) {
                placeWidth = (int) graphics.getClipBounds().getWidth();
                placeHeight = (int) graphics.getClipBounds().getHeight();
                super.paint(graphics);
                drawGrid(graphics, placeWidth, placeHeight);
                try {
                    customDrawPlace(graphics, placeWidth, placeHeight);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };

        placeCanvas.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                try {
                    Index mouse = new Index(e.getX() - placeWidth / 2, placeHeight / 2 - e.getY());
                    mouseLabel.setText("Mouse Position: " + mouse.getX() + ":" + mouse.getY());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        placeCanvas.setBackground(Color.WHITE);
        pane.add(new JScrollPane(placeCanvas), BorderLayout.CENTER);
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

    private void customDrawVelocity(Graphics graphics, int width, int height) {
        Index zero = toCoordinate(new DecimalPosition(0, 0), width, height);

        // Protagonist velocity
        Index protagonistVelocity = toCoordinate(velocityObstacleManager.getProtagonist().getVelocity(), width, height);
        graphics.setColor(Color.GREEN);
        graphics.drawLine(zero.getX(), zero.getY(), protagonistVelocity.getX(), protagonistVelocity.getY());

        for (OrcaLine orcaLine : velocityObstacleManager.getOrcaLines()) {
//            // VO
//            graphics.setColor(Color.RED);
//            Index relativePosition = toCoordinate(orcaLine.getRelativePosition(), width, height);
//            int combinedRadius = (int) orcaLine.getCombinedRadius();
//            graphics.drawArc(relativePosition.getX() - combinedRadius, relativePosition.getY() - combinedRadius, 2 * combinedRadius, 2 * combinedRadius, 0, 360);
//
//            Index truncationMiddle = toCoordinate(orcaLine.getTruncationMiddle(), width, height);
//            int truncationRadius = (int) orcaLine.getTruncationRadius();
//            graphics.drawArc(truncationMiddle.getX() - truncationRadius, truncationMiddle.getY() - truncationRadius, 2 * truncationRadius, 2 * truncationRadius, 0, 360);
//
//            // Relative velocity
//            Index relativeVelocity = toCoordinate(orcaLine.getRelativeVelocity(), width, height);
//            graphics.drawLine(zero.getX(), zero.getY(), relativeVelocity.getX(), relativeVelocity.getY());

            // u
            //graphics.setColor(Color.BLUE);
            //Index u = toCoordinate(orcaLine.getU().getPosition().add(orcaLine.getRelativeVelocity().getPosition()));
            //graphics.drawLine(relativeVelocity.getX(), relativeVelocity.getY(), u.getX(), u.getY());

//            // Cone leg
//            if (orcaLine.getConeLine() != null) {
//                // graphics.setColor(Color.ORANGE);
//                Index end = toCoordinate(orcaLine.getConeLine().getPoint2(), width, height);
//                graphics.drawLine(zero.getX(), zero.getY(), end.getX(), end.getY());
//            }
//            graphics.setColor(Color.MAGENTA);
//            drawCross(graphics, toCoordinate(orcaLine.getPoint().getPosition()));
//            graphics.setColor(Color.BLUE);
//            drawCross(graphics, toCoordinate(orcaLine.getDirection().getPosition()));

//            graphics.setColor(Color.GREEN);
//            Index projection = toCoordinate(orcaLine.getProjectionOnVelocityObstacle(),  width, height);
//            drawCross(graphics, projection);

            Index point = toCoordinate(orcaLine.getPoint(), width, height);
            // drawCross(graphics, point);
            Index direction = toCoordinate(orcaLine.getDirection(), width, height);
            //graphics.setColor(Color.YELLOW);
            //graphics.drawLine(point.getX(), point.getY(), direction.getX(), direction.getY());
            Index lp1 = direction.rotateCounterClock(point, MathHelper.QUARTER_RADIANT);
            Index lp2 = direction.rotateCounterClock(point, MathHelper.THREE_QUARTER_RADIANT);
            Index lp3 = point.rotateCounterClock(lp1, -MathHelper.QUARTER_RADIANT);
            Index lp4 = point.rotateCounterClock(lp2, -MathHelper.THREE_QUARTER_RADIANT);
            //Index oppositeDir = orcaLine.getPoint().getPosition().sub(orcaLine.getDirection().getPosition());
            //Index lp3 = lp1.add(oppositeDir);
            // Index lp4 = lp2.add(oppositeDir);

            //graphics.setColor(Color.MAGENTA);
            ///graphics.drawLine(lp1.getX(), lp1.getY(), lp2.getX(), lp2.getY());

            int[] xPoints = {lp1.getX(), lp2.getX(), lp4.getX(), lp3.getX()};
            int[] yPoints = {lp1.getY(), lp2.getY(), lp4.getY(), lp3.getY()};
            graphics.setColor(new Color(0f, 0f, 0f, 0.5f));
            graphics.fillPolygon(xPoints, yPoints, xPoints.length);


            //Index direction = toCoordinate(orcaLine.getDirection().getPosition(), width, height);
            //Index point = toCoordinate(orcaLine.getPoint().getPosition(), width, height);
            //graphics.setColor(Color.BLUE);
            //graphics.drawLine(direction.getX(), direction.getY(), point.getX(), point.getY());
            //Index lp1 = direction.rotateCounterClock(point, MathHelper.QUARTER_RADIANT);
            //Index lp2 = direction.rotateCounterClock(point, MathHelper.THREE_QUARTER_RADIANT);
            //graphics.setColor(Color.BLUE);
            //graphics.drawLine(lp1.getX(), lp1.getY(), lp2.getX(), lp2.getY());
        }

        // Preferred velocity
        graphics.setColor(Color.MAGENTA);
        Index targetOrigin = toCoordinate(new DecimalPosition(target).normalize(SyncItem.SPEED).sub(protagonist.getDecimalPosition()), width, height);
        graphics.drawLine(zero.getX(), zero.getY(), targetOrigin.getX(), targetOrigin.getY());
        // Optimized velocity
        graphics.setColor(Color.BLUE);
        if (optimizedVelocity != null) {
            Index optimizedVelocityOrigin = toCoordinate(optimizedVelocity, width, height);
            graphics.drawLine(zero.getX(), zero.getY(), optimizedVelocityOrigin.getX(), optimizedVelocityOrigin.getY());
        }
    }

    private void customDrawPlace(Graphics graphics, int width, int height) {
        // Protagonist
        graphics.setColor(Color.GREEN);
        Index protagonistPosition = toCoordinate(protagonist.getDecimalPosition(), width, height);
        graphics.drawArc(protagonistPosition.getX() - protagonist.getRadius(), protagonistPosition.getY() - protagonist.getRadius(), 2 * protagonist.getRadius(), 2 * protagonist.getRadius(), 0, 360);
        Index protagonistVelocity = toCoordinate(protagonist.getDecimalPosition().add(protagonist.getVelocity()), width, height);
        graphics.drawLine(protagonistPosition.getX(), protagonistPosition.getY(), protagonistVelocity.getX(), protagonistVelocity.getY());
        graphics.setColor(Color.BLACK);
        Index target = toCoordinate(protagonist.getTargetPosition(), width, height);
        graphics.drawLine(protagonistPosition.getX(), protagonistPosition.getY(), target.getX(), target.getY());
        // Optimized velocity
        graphics.setColor(Color.MAGENTA);
        if (optimizedVelocity != null) {
            Index optimizedVelocityOrigin = toCoordinate(optimizedVelocity.add(protagonist.getDecimalPosition()), width, height);
            graphics.drawLine(protagonistPosition.getX(), protagonistPosition.getY(), optimizedVelocityOrigin.getX(), optimizedVelocityOrigin.getY());
        }

        // Other
        for (SyncItem syncItem : syncItems) {
            graphics.setColor(Color.BLUE);
            Index syncItemPosition = toCoordinate(syncItem.getDecimalPosition(), width, height);
            graphics.drawArc(syncItemPosition.getX() - syncItem.getRadius(), syncItemPosition.getY() - syncItem.getRadius(), 2 * syncItem.getRadius(), 2 * syncItem.getRadius(), 0, 360);
            Index syncItemVelocity = toCoordinate(syncItem.getDecimalPosition().add(syncItem.getVelocity()), width, height);
            graphics.drawLine(syncItemPosition.getX(), syncItemPosition.getY(), syncItemVelocity.getX(), syncItemVelocity.getY());
        }
    }

    private Index toCoordinate(DecimalPosition index, int width, int height) {
        return new Index((int) index.getX() + width / 2, height / 2 - (int) index.getY());
    }

    public static void main(String[] args) {
        new StaticMain();
    }

    private void repaint() {
        if (velocityCanvas != null) {
            velocityCanvas.repaint();
        }
        if (placeCanvas != null) {
            placeCanvas.repaint();
        }
        //xSpinner.setValue(protagonist.getVelocity().getX());
        //ySpinner.setValue(protagonist.getVelocity().getY());
    }

    private void drawCross(Graphics graphics, Index position) {
        graphics.drawLine(position.getX() - CROSS_SIZE, position.getY(), position.getX() + CROSS_SIZE, position.getY());
        graphics.drawLine(position.getX(), position.getY() - CROSS_SIZE, position.getX(), position.getY() + CROSS_SIZE);
    }
}