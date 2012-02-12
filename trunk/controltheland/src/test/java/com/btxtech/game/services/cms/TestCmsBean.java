package com.btxtech.game.services.cms;

import org.springframework.stereotype.Component;

/**
 * User: beat
 * Date: 13.02.2012
 * Time: 00:36:37
 */
@Component("testCmsBean")
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

}
