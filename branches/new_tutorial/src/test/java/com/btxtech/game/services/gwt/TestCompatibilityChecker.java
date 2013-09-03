package com.btxtech.game.services.gwt;

import com.btxtech.game.jsre.client.CompatibilityChecker;
import com.btxtech.game.jsre.client.common.Constants;
import junit.framework.Assert;
import org.junit.Test;

/**
 * User: beat
 * Date: 26.04.13
 * Time: 11:40
 */
public class TestCompatibilityChecker {
    @Test
    public void getServerVersion() throws Exception {
        CompatibilityChecker compatibilityChecker = new CompatibilityCheckerImpl();
        Assert.assertEquals(Constants.INTERFACE_VERSION, compatibilityChecker.getServerVersion());
    }
}
