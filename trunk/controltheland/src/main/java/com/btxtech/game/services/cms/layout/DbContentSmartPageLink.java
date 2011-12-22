package com.btxtech.game.services.cms.layout;

import com.btxtech.game.services.cms.page.DbPage;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

/**
 * User: beat
 * Date: 24.07.2011
 * Time: 17:44:32
 */
@Entity
@DiscriminatorValue("SMART_PAGE_LINK")
public class DbContentSmartPageLink extends DbContent {
    @ManyToOne(fetch = FetchType.LAZY)
    private DbPage dbPage;
    private Access enableAccess;
    private String buttonName;
    private String accessDeniedString;
    private String springBeanName;
    private String propertyExpression;
    private String string0;
    private String string1;
    private String stringN;

    public Access getEnableAccess() {
        return enableAccess;
    }

    public void setEnableAccess(Access enableAccess) {
        this.enableAccess = enableAccess;
    }

    public String getButtonName() {
        return buttonName;
    }

    public void setButtonName(String buttonName) {
        this.buttonName = buttonName;
    }

    public DbPage getDbPage() {
        return dbPage;
    }

    public void setDbPage(DbPage dbPage) {
        this.dbPage = dbPage;
    }

    public String getAccessDeniedString() {
        return accessDeniedString;
    }

    public void setAccessDeniedString(String accessDeniedString) {
        this.accessDeniedString = accessDeniedString;
    }

    public String getSpringBeanName() {
        return springBeanName;
    }

    public void setSpringBeanName(String springBeanName) {
        this.springBeanName = springBeanName;
    }

    public String getPropertyExpression() {
        return propertyExpression;
    }

    public void setPropertyExpression(String propertyExpression) {
        this.propertyExpression = propertyExpression;
    }

    public String getString0() {
        return string0;
    }

    public void setString0(String string0) {
        this.string0 = string0;
    }

    public String getString1() {
        return string1;
    }

    public void setString1(String string1) {
        this.string1 = string1;
    }

    public String getStringN() {
        return stringN;
    }

    public void setStringN(String stringN) {
        this.stringN = stringN;
    }
}
