package com.btxtech.game.jsre.common;

import org.junit.Assert;
import org.junit.Test;

/**
 * User: beat
 * Date: 26.01.14
 * Time: 18:22
 */
public class TestFacebookUtils {
    @Test
    public void createNickNameSuggestion() {
        Assert.assertTrue(FacebookUtils.createNickNameSuggestion(null, null).startsWith("Gamer"));

        Assert.assertTrue(FacebookUtils.createNickNameSuggestion("qaywsx", "poiuzt").startsWith("qaypoi"));
        Assert.assertTrue(FacebookUtils.createNickNameSuggestion(null, "poiuzt").startsWith("poi"));
        Assert.assertTrue(FacebookUtils.createNickNameSuggestion("qaywsx", null).startsWith("qay"));

        Assert.assertTrue(FacebookUtils.createNickNameSuggestion("qa", "p").startsWith("qap"));
        Assert.assertTrue(FacebookUtils.createNickNameSuggestion(null, "po").startsWith("po"));
        Assert.assertTrue(FacebookUtils.createNickNameSuggestion("qx", null).startsWith("q"));

        Assert.assertTrue(FacebookUtils.createNickNameSuggestion("", "").startsWith("Gamer"));
        Assert.assertTrue(FacebookUtils.createNickNameSuggestion(null, "").startsWith("Gamer"));
        Assert.assertTrue(FacebookUtils.createNickNameSuggestion("", null).startsWith("Gamer"));
    }

}
