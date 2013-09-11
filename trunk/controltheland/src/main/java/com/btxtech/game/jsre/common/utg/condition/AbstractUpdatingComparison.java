package com.btxtech.game.jsre.common.utg.condition;

import com.btxtech.game.jsre.common.gameengine.services.GlobalServices;

/**
 * User: beat
 * Date: 07.09.13
 * Time: 11:31
 */
public abstract class AbstractUpdatingComparison implements AbstractComparison {
    private static int MIN_SEND_DELAY = 1000;
    private long lastProgressSendTime;
    private boolean hasUpdateToSend;
    private GlobalServices globalServices;

    public void setGlobalServices(GlobalServices globalServices) {
        this.globalServices = globalServices;
    }

    protected void onProgressChanged() {
        if (lastProgressSendTime + MIN_SEND_DELAY > System.currentTimeMillis()) {
            hasUpdateToSend = true;
            return;
        }
        if (globalServices != null) {
            globalServices.getConditionService().sendProgressUpdate(getAbstractConditionTrigger().getActor(), getAbstractConditionTrigger().getIdentifier());
            lastProgressSendTime = System.currentTimeMillis();
            hasUpdateToSend = false;
        }
    }

    @Override
    public void handleDeferredUpdate() {
        if (hasUpdateToSend) {
            onProgressChanged();
        }
    }

}
