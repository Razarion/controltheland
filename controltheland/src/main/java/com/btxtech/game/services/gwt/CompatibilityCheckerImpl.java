package com.btxtech.game.services.gwt;

import com.btxtech.game.jsre.client.CompatibilityChecker;
import com.btxtech.game.jsre.client.common.Constants;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * User: beat
 * Date: 26.04.13
 * Time: 10:33
 */
public class CompatibilityCheckerImpl extends RemoteServiceServlet implements CompatibilityChecker {
    @Override
    public int getServerVersion() {
        return Constants.INTERFACE_VERSION;
    }
}
