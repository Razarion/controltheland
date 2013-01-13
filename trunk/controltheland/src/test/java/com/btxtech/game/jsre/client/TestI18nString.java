package com.btxtech.game.jsre.client;

import junit.framework.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * User: beat
 * Date: 13.01.13
 * Time: 18:32
 */
public class TestI18nString {

    @Test
    public void testSimple() {
        // Setup
        I18nString i18nString1 = new I18nString(null);
        Map<I18nString.Language, String> localizedStrings = new HashMap<>();
        localizedStrings.put(I18nString.Language.DEFAULT, "default");
        localizedStrings.put(I18nString.Language.DE, "de");
        I18nString i18nString2 = new I18nString(localizedStrings);
        localizedStrings = new HashMap<>();
        localizedStrings.put(I18nString.Language.DEFAULT, "default");
        I18nString i18nString3 = new I18nString(localizedStrings);
        // Verify
        Assert.assertEquals(null, i18nString1.getString());
        Assert.assertEquals(null, i18nString1.getString(I18nString.Language.DEFAULT));
        Assert.assertEquals(null, i18nString1.getString(I18nString.Language.DE));

        Assert.assertEquals("default", i18nString2.getString());
        Assert.assertEquals("default", i18nString2.getString(I18nString.Language.DEFAULT));
        Assert.assertEquals("de", i18nString2.getString(I18nString.Language.DE));

        Assert.assertEquals("default", i18nString3.getString());
        Assert.assertEquals("default", i18nString3.getString(I18nString.Language.DEFAULT));
        Assert.assertEquals("default", i18nString3.getString(I18nString.Language.DE));
    }

}
