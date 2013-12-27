package com.btxtech.game.services.common;

import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.db.DbProperty;
import junit.framework.Assert;
import org.hibernate.HibernateException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 05.06.13
 * Time: 17:20
 */
public class TestPropertyService extends AbstractServiceTest {
    @Autowired
    private PropertyService propertyService;

    @Test
    @DirtiesContext
    public void testIntPropertyNoSession() throws Exception {
        try {
            propertyService.getIntProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST);
            Assert.fail("HibernateException expected");
        } catch (HibernateException e) {
            Assert.assertEquals("No Session found for current thread", e.getMessage());
        }
    }

    @Test
    @DirtiesContext
    public void testStringPropertyNoSession() throws Exception {
        try {
            propertyService.getStringProperty(PropertyServiceEnum.FACEBOOK_OPTIONAL_AD_URL_KEY);
            Assert.fail("HibernateException expected");
        } catch (HibernateException e) {
            Assert.assertEquals("No Session found for current thread", e.getMessage());
        }
    }

    @Test
    @DirtiesContext
    public void testIntPropertyNoProperty() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            propertyService.getIntProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST);
            Assert.fail("NoSuchPropertyException expected");
        } catch (NoSuchPropertyException e) {
            Assert.assertEquals("No such property: PropertyServiceEnum{displayName='Crystal cost for creating a guild', type=class java.lang.Integer, fallbackValue=null}", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testIntPropertyFallback() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(240000, propertyService.getIntPropertyFallback(PropertyServiceEnum.REGISTER_DIALOG_DELAY));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testStringPropertyNoProperty() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            propertyService.getStringProperty(PropertyServiceEnum.FACEBOOK_OPTIONAL_AD_URL_KEY);
            Assert.fail("NoSuchPropertyException expected");
        } catch (NoSuchPropertyException e) {
            Assert.assertEquals("No such property: PropertyServiceEnum{displayName='Facebook optional ad key', type=class java.lang.String, fallbackValue=null}", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testCreateIntProperty() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, propertyService.getIntProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testCreateStringProperty() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.FACEBOOK_OPTIONAL_AD_URL_KEY, "qwert");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals("qwert", propertyService.getStringProperty(PropertyServiceEnum.FACEBOOK_OPTIONAL_AD_URL_KEY));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testCreatePropertyWrongType() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, "Hallo");
            Assert.fail("WrongPropertyTypeException expected");
        } catch (WrongPropertyTypeException e) {
            Assert.assertEquals("Wrong property type exception. Expected: class java.lang.String Property: PropertyServiceEnum{displayName='Crystal cost for creating a guild', type=class java.lang.Integer, fallbackValue=null}", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void getDPropertyCrudServiceHelper() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbProperty dbProperty = propertyService.getDPropertyCrudServiceHelper().createDbChild();
        dbProperty.setPropertyServiceEnum(PropertyServiceEnum.GUILD_CRYSTAL_COST);
        dbProperty.setIntegerValue(15);
        propertyService.getDPropertyCrudServiceHelper().updateDbChild(dbProperty);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(15, propertyService.getIntProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void setValueAsString4Integer() throws Exception {
        DbProperty dbIntProperty = new DbProperty();
        try {
            dbIntProperty.setValueAsString("Hallo");
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("propertyServiceEnum is not set", e.getMessage());
        }
        dbIntProperty.setPropertyServiceEnum(PropertyServiceEnum.GUILD_CRYSTAL_COST);
        try {
            dbIntProperty.setValueAsString("Hallo");
            Assert.fail("NumberFormatException expected");
        } catch (NumberFormatException e) {
            Assert.assertEquals("For input string: \"Hallo\"", e.getMessage());
        }
        dbIntProperty.setValueAsString("111");
        Assert.assertEquals(111, (int) dbIntProperty.getIntegerValue());
    }

    @Test
    @DirtiesContext
    public void setValueAsString4String() throws Exception {
        DbProperty dbIntProperty = new DbProperty();
        try {
            dbIntProperty.setValueAsString("Hallo");
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("propertyServiceEnum is not set", e.getMessage());
        }
        dbIntProperty.setPropertyServiceEnum(PropertyServiceEnum.FACEBOOK_OPTIONAL_AD_URL_KEY);
        dbIntProperty.setValueAsString("Hallo");
        Assert.assertEquals("Hallo", dbIntProperty.getStringValue());
    }

    @Test
    @DirtiesContext
    public void getValueAsString() throws Exception {
        DbProperty dbIntProperty = new DbProperty();
        dbIntProperty.setPropertyServiceEnum(PropertyServiceEnum.GUILD_CRYSTAL_COST);
        dbIntProperty.setIntegerValue(111);
        Assert.assertEquals("111", dbIntProperty.getValueAsString());
        DbProperty dbStringProperty = new DbProperty();
        dbStringProperty.setPropertyServiceEnum(PropertyServiceEnum.FACEBOOK_OPTIONAL_AD_URL_KEY);
        dbStringProperty.setStringValue("Hallo");
        Assert.assertEquals("Hallo", dbStringProperty.getValueAsString());
    }

}
