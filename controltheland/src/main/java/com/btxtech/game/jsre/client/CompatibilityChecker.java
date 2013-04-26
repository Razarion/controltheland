package com.btxtech.game.jsre.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * User: beat
 * Date: 26.04.13
 * Time: 10:14
 */
@RemoteServiceRelativePath("compatibilityChecker")
public interface CompatibilityChecker extends RemoteService {
    int getServerVersion();
}
