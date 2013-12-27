package com.btxtech.game.jsre.client.dialogs;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * User: beat
 * Date: 22.04.13
 * Time: 22:59
 */
public abstract class PeriodicDialog extends Dialog {
    private Timer timer;
    private int period;

    public PeriodicDialog(String title) {
        super(title);
        addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> popupPanelCloseEvent) {
                if (isReshowNeeded() && timer != null) {
                    timer.schedule(period);
                }
            }
        });
    }

    /**
     * Override in subclasses
     *
     * @return true if dialog should be showed or scheduled for showing
     */
    protected boolean isReshowNeeded() {
        return true;
    }

    public void start(boolean showImmediately, int period) {
        this.period = period;
        stopTimer();
        if (!isReshowNeeded()) {
            return;
        }

        timer = new Timer() {
            @Override
            public void run() {
                if (isReshowNeeded()) {
                    DialogManager.showDialog(PeriodicDialog.this, DialogManager.Type.QUEUE_ABLE);
                }
            }
        };
        if (showImmediately) {
            DialogManager.showDialog(PeriodicDialog.this, DialogManager.Type.QUEUE_ABLE);
        } else {
            timer.schedule(period);
        }
    }

    public void stop() {
        stopTimer();
        if (isShowing()) {
            close();
        }
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

}
