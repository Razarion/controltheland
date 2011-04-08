package com.btxtech.game.services.bot;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.services.BaseTestService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 28.03.2011
 * Time: 17:10:51
 */
public class TestBotService extends BaseTestService {
    @Autowired
    private BotService botService;

    @Test
    @DirtiesContext
    public void testInRealm() throws Exception {
        configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        setupMinimalBot(new Rectangle(1, 1, 3000, 3000), new Rectangle(1000, 1000, 1000, 1000));
        setupMinimalBot(new Rectangle(4000, 4000, 3000, 3000), new Rectangle(5000, 5000, 1000, 1000));

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Assert.assertTrue(botService.isInRealm(new Index(2,2)));
        Assert.assertTrue(botService.isInRealm(new Index(2999,2999)));
        Assert.assertTrue(botService.isInRealm(new Index(4001,4001)));
        Assert.assertTrue(botService.isInRealm(new Index(6999,6999)));

        Assert.assertFalse(botService.isInRealm(new Index(3002,3002)));
        Assert.assertFalse(botService.isInRealm(new Index(3999,3999)));
        Assert.assertFalse(botService.isInRealm(new Index(7001,7001)));
        Assert.assertFalse(botService.isInRealm(new Index(8000,8000)));

    }
}
