package gui; /**
 * User: beat
 * Date: 21.03.13
 * Time: 23:34
 */

import com.btxtech.game.jsre.common.gameengine.services.collision.impl.NoBetterPathFoundException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import model.MovingModel;
import scenario.Scenario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MovingGui extends AbstractGui {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;
    public static MovingGui instance;

    public MovingGui() {
        instance = this;
    }

    @Override
    protected void addToMenuBar(JPanel menu) {
        JButton restartButton = new JButton("Restart");
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    getMovingModel().restart();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
        menu.add(restartButton);

        final JToggleButton pauseButton = new JToggleButton("Pasue");
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getMovingModel().pause(((JToggleButton) e.getSource()).isSelected());
            }
        });
        menu.add(pauseButton);

        final JTextField stepFactor = new JTextField();
        stepFactor.setText("0.1");

        JButton tickButton = new JButton("Step");
        tickButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    getMovingModel().step(Double.parseDouble(stepFactor.getText()));
                    pauseButton.setSelected(true);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        menu.add(tickButton);
        menu.add(stepFactor);
    }

    @Override
    protected void customDraw(final Graphics graphics) {
        if (getMovingModel() != null) {
            getMovingModel().iterateOverSyncItems(new MovingModel.SyncItemCallback() {
                @Override
                public void onSyncItem(SyncItem syncItem) {
                    drawSyncItem(graphics, getMovingModel());
                    drawOriginalPath(graphics);
                }
            });
        }
    }

    @Override
    protected void onScenarioChanged(Scenario scenario) throws NoBetterPathFoundException {
        getMovingModel().setScenario(scenario);
    }

    private void drawOriginalPath(Graphics graphics) {
    /*    graphics.setColor(new Color(255, 100, 0, 100));
        for (SyncItem syncItem : getMovingModel().getSyncItems()) {
            if (syncItem.getState() == SyncItem.MoveState.MOVING) {
                Index position = syncItem.getPosition();
                Index aim = syncItem.calculateMoveToTarget(0.5).getPosition();
                graphics.drawLine(position.getX(), position.getY(), aim.getX(), aim.getY());
            }
        }*/
    }

    public static MovingGui getInstance() {
        return instance;
    }
}
