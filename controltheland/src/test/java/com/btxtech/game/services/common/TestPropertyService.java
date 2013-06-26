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
            propertyService.getIntProperty(PropertyServiceEnum.GUILD_RAZARION_COST);
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
            propertyService.getIntProperty(PropertyServiceEnum.GUILD_RAZARION_COST);
            Assert.fail("NoSuchPropertyException expected");
        } catch (NoSuchPropertyException e) {
            Assert.assertEquals("No such property: PropertyServiceEnum{displayName='Razarion cost for creating a guild', type=class java.lang.Integer}", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testCreateIntProperty() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_RAZARION_COST, 1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, propertyService.getIntProperty(PropertyServiceEnum.GUILD_RAZARION_COST));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testCreatePropertyWrongType() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            propertyService.createProperty(PropertyServiceEnum.GUILD_RAZARION_COST, "Hallo");
            Assert.fail("WrongPropertyTypeException expected");
        } catch (WrongPropertyTypeException e) {
            Assert.assertEquals("Wrong property type exception. Expected: class java.lang.String Property: PropertyServiceEnum{displayName='Razarion cost for creating a guild', type=class java.lang.Integer}", e.getMessage());
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
        dbProperty.setPropertyServiceEnum(PropertyServiceEnum.GUILD_RAZARION_COST);
        dbProperty.setIntegerValue(15);
        propertyService.getDPropertyCrudServiceHelper().updateDbChild(dbProperty);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(15, propertyService.getIntProperty(PropertyServiceEnum.GUILD_RAZARION_COST));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void setValueAsString() throws Exception {
        DbProperty dbProperty = new DbProperty();
        try {
            dbProperty.setValueAsString("Hallo");
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("propertyServiceEnum is not set", e.getMessage());
        }
        dbProperty.setPropertyServiceEnum(PropertyServiceEnum.GUILD_RAZARION_COST);
        try {
            dbProperty.setValueAsString("Hallo");
            Assert.fail("NumberFormatException expected");
        } catch (NumberFormatException e) {
            Assert.assertEquals("For input string: \"Hallo\"", e.getMessage());
        }
        dbProperty.setValueAsString("111");
        Assert.assertEquals(111, (int)dbProperty.getIntegerValue());
    }

    @Test
    @DirtiesContext
    public void getValueAsString() throws Exception {
        DbProperty dbProperty = new DbProperty();
        dbProperty.setPropertyServiceEnum(PropertyServiceEnum.GUILD_RAZARION_COST);
        dbProperty.setIntegerValue(111);
        Assert.assertEquals("111", dbProperty.getValueAsString());
    }

}
