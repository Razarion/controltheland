package com.btxtech.game.jsre.client.utg.tip;

import com.google.gwt.user.client.Timer;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 20.11.2011
 * Time: 23:26:54
 */
public class TipManager {
    private static final TipManager INSTANCE = new TipManager();
    private Timer timer;
    private List<TipEntry> tips = new ArrayList<TipEntry>();
    private TipEntry activeTip;
    private static Logger log = Logger.getLogger(TipManager.class.getName());

    public static TipManager getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private TipManager() {
        tips.add(new ImageTipEntry(17000, 3000, "Maximize.jpg"));
        tips.add(new AttackEnemyTipEntry(5000, 2000, false));
        tips.add(new ImageTipEntry(17000, 3000, "ArrowKeys.jpg"));
        tips.add(new AttackEnemyTipEntry(30000, 2000, true));
    }

    public void activate() {
        deactivate();
        displayNextTip();
    }

    private void displayNextTip() {
        if (tips.isEmpty()) {
            deactivate();
            return;
        }
        final TipEntry nextTip = tips.get(0);
        if (!nextTip.isRepeat()) {
            tips.remove(0);
        }
        timer = new Timer() {
            @Override
            public void run() {
                showActiveTipPanel(nextTip);
                timer = new Timer() {

                    @Override
                    public void run() {
                        closeActiveTipPanel();
                        displayNextTip();
                    }
                };
                timer.schedule(nextTip.getShowTime());
            }
        };
        timer.schedule(nextTip.getDelay());
    }

    public void deactivate() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        closeActiveTipPanel();
    }

    private void showActiveTipPanel(TipEntry tip) {
        try {
            closeActiveTipPanel();
            activeTip = tip;
            activeTip.show();
        } catch (Throwable t) {
            log.log(Level.SEVERE, "showActiveTipPanel", t);
        }
    }

    private void closeActiveTipPanel() {
        try {
            if (activeTip != null) {
                activeTip.close();
                activeTip = null;
            }
        } catch (Throwable t) {
            log.log(Level.SEVERE, "closeActiveTipPanel", t);
        }
    }


}
