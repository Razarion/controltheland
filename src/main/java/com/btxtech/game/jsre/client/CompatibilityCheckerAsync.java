package com.btxtech.game.jsre.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * User: beat
 * Date: 26.04.13
 * Time: 10:14
 */
public interface CompatibilityCheckerAsync {
    void getServerVersion(AsyncCallback<Integer> async);
}
