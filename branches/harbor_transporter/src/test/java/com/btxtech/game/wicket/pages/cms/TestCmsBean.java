package com.btxtech.game.wicket.pages.cms;

import com.btxtech.game.services.common.db.DbI18nString;
import org.junit.Ignore;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * User: beat
 * Date: 13.02.2012
 * Time: 00:36:37
 */
@Component("testCmsBean")
@Ignore
public class TestCmsBean {
    public double getDouble1() {
        return 1.333333;
    }

    public double getDouble2() {
        return 2.0;
    }

    public double getDouble3() {
        return 5.8;
    }

    public Double getDouble4() {
        return 4.8;
    }

    public int getInteger1() {
        return 10;
    }

    public Integer getInteger2() {
        return 11;
    }

    public DbI18nString getDbI18nString() {
        DbI18nString dbI18nString = new DbI18nString();
        dbI18nString.putString("Hello");
        dbI18nString.putString(Locale.GERMAN, "Hallo");
        return dbI18nString;
    }
}
