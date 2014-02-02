package com.btxtech.game.jsre.client.utg.tip;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.utg.ClientUserTracker;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * User: beat
 * Date: 19.09.13
 * Time: 11:18
 */
public class StorySplashPopup extends PopupPanel {
    private static final int REMOVE_TIMEOUT = 500;
    private StorySplashPanel storySplashPanel;
    private Timer removeTimer;
    private AbstractSplashPopupInfo abstractSplashPopupInfo;

    public StorySplashPopup(AbstractSplashPopupInfo abstractSplashPopupInfo) {
        this.abstractSplashPopupInfo = abstractSplashPopupInfo;
        setStyleName("storyPopup");
        storySplashPanel = new StorySplashPanel(abstractSplashPopupInfo);
        setWidget(storySplashPanel);
        getElement().getStyle().setZIndex(Constants.Z_INDEX_TIP_POPUP);
        fadeIn();
    }

    public void fadeOut() {
        if (removeTimer != null) {
            return;
        }
        removeStyleName("raz-fade-in");
        addStyleName("raz-fade-out");
        removeTimer = new Timer() {
            @Override
            public void run() {
                if (isShowing()) {
                    ClientUserTracker.getInstance().onDialogDisappears(StorySplashPopup.this);
                }
                hide();
            }
        };
        removeTimer.schedule(REMOVE_TIMEOUT);
    }

    public void fadeIn() {
        stopRemoveTimer();
        removeStyleName("raz-fade-out");
        addStyleName("raz-fade-in");
        boolean showing =  isShowing();
        center();
        if (!showing) {
            ClientUserTracker.getInstance().onDialogAppears(this, abstractSplashPopupInfo.getTitle());
        }
    }

    private void stopRemoveTimer() {
        if (removeTimer != null) {
            removeTimer.cancel();
            removeTimer = null;
        }
    }

    public void setTaskText(String taskText) {
        storySplashPanel.setTaskText(taskText);
    }
}
