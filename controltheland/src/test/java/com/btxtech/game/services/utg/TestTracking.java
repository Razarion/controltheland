package com.btxtech.game.services.utg;

import com.btxtech.game.jsre.client.MovableService;
import com.btxtech.game.services.AbstractServiceTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 10.04.2011
 * Time: 15:42:16
 */
public class TestTracking extends AbstractServiceTest {
    @Autowired
    private UserTrackingService userTrackingService;
    @Autowired
    private MovableService movableService;


    @Test
    @DirtiesContext
    public void testSimple() throws Exception {
        configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        String sessionId = getHttpSessionId();
        movableService.getGameInfo();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // List<VisitorInfo> visitorInfo = userTrackingService.getVisitorInfos(UserTrackingFilter.newDefaultFilter());
        VisitorDetailInfo visitorDetailInfo = userTrackingService.getVisitorDetails(sessionId);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}
