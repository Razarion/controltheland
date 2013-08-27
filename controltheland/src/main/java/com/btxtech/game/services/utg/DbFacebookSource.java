package com.btxtech.game.services.utg;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;

/**
 * User: beat
 * Date: 16.07.13
 * Time: 16:22
 */
@Embeddable
public class DbFacebookSource {
    @Column(length = 2000)
    @Basic(fetch= FetchType.LAZY)
    private String wholeString;
    @Basic(fetch=FetchType.LAZY)
    private String fbSource;
    @Basic(fetch=FetchType.LAZY)
    private String optionalAdValue;

    public String getFbSource() {
        return fbSource;
    }

    public void setFbSource(String fbSource) {
        this.fbSource = fbSource;
    }

    public String getWholeString() {
        return wholeString;
    }

    public void setWholeString(String wholeString) {
        this.wholeString = wholeString;
    }

    public void setOptionalAdValue(String optionalAdValue) {
        this.optionalAdValue = optionalAdValue;
    }

    public String getOptionalAdValue() {
        return optionalAdValue;
    }
}
