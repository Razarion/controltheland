package gui; /**
 * User: beat
 * Date: 21.03.13
 * Time: 23:34
 */

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.services.collision.impl.NoBetterPathFoundException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import scenario.Scenario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MovingGui extends AbstractGui {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;

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
    protected void customDraw(Graphics graphics) {
        if (getMovingModel() != null) {
            synchronized (getMovingModel().getSyncItems()) {
                drawSyncItem(graphics, getMovingModel().getSyncItems());
                drawOriginalPath(graphics);
            }
        }
    }

    @Override
    protected void onScenarioChanged(Scenario scenario) throws NoBetterPathFoundException {
        getMovingModel().setScenario(scenario);
    }

    private void drawOriginalPath(Graphics graphics) {
        graphics.setColor(new Color(255, 100, 0, 100));
        for (SyncItem syncItem : getMovingModel().getSyncItems()) {
            if (syncItem.getState() == SyncItem.MoveState.MOVING) {
                Index previous = syncItem.getPosition();
                for (Index next : syncItem.getPath().getNextWayPositions()) {
                    graphics.drawLine(previous.getX(), previous.getY(), next.getX(), next.getY());
                    previous = next;
                }
            }
        }
    }

}
