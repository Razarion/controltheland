package com.btxtech.game.services.common.impl;

import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.common.NoSuchPropertyException;
import com.btxtech.game.services.common.PropertyService;
import com.btxtech.game.services.common.PropertyServiceEnum;
import com.btxtech.game.services.common.WrongPropertyTypeException;
import com.btxtech.game.services.common.db.DbProperty;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * User: beat
 * Date: 05.06.13
 * Time: 00:58
 */
@Component("propertyService")
public class PropertyServiceImpl implements PropertyService {
    @Autowired
    private CrudRootServiceHelper<DbProperty> dbPropertyCrudRootServiceHelper;
    @Autowired
    private SessionFactory sessionFactory;
    private Log log = LogFactory.getLog(PropertyServiceImpl.class);

    @PostConstruct
    public void init() {
        dbPropertyCrudRootServiceHelper.init(DbProperty.class);
    }

    @Override
    public int getIntProperty(PropertyServiceEnum propertyServiceEnum) throws NoSuchPropertyException, WrongPropertyTypeException {
        if (!(Integer.class.isAssignableFrom(propertyServiceEnum.getType()))) {
            throw new WrongPropertyTypeException(Integer.class, propertyServiceEnum);
        }
        return getProperty(propertyServiceEnum).getIntegerValue();
    }

    @Override
    public int getIntPropertyFallback(PropertyServiceEnum propertyServiceEnum) {
        try {
            return getIntProperty(propertyServiceEnum);
        } catch (NoSuchPropertyException | WrongPropertyTypeException e) {
            // ExceptionHandler.handleException(e, "No property defined for: " + propertyServiceEnum + " using default value: " + propertyServiceEnum.getFallbackValue());
            return (int) propertyServiceEnum.getFallbackValue();
        }
    }

    @Override
    public String getStringProperty(PropertyServiceEnum propertyServiceEnum) throws NoSuchPropertyException, WrongPropertyTypeException {
        if (!(String.class.isAssignableFrom(propertyServiceEnum.getType()))) {
            throw new WrongPropertyTypeException(String.class, propertyServiceEnum);
        }
        return getProperty(propertyServiceEnum).getStringValue();
    }

    @SuppressWarnings("unchecked")
    private DbProperty getProperty(PropertyServiceEnum propertyServiceEnum) throws NoSuchPropertyException {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbProperty.class);
        criteria.add(Restrictions.eq("propertyServiceEnum", propertyServiceEnum));
        List<DbProperty> properties = criteria.list();
        if (properties == null || properties.isEmpty()) {
            throw new NoSuchPropertyException(propertyServiceEnum);
        } else {
            if (properties.size() > 1) {
                log.warn("PropertyServiceImpl.getProperty(): more then one property in DB: " + propertyServiceEnum);
            }
            return properties.get(0);
        }
    }

    @Override
    public void createProperty(PropertyServiceEnum propertyServiceEnum, Object value) throws WrongPropertyTypeException {
        if (!(value.getClass().isAssignableFrom(propertyServiceEnum.getType()))) {
            throw new WrongPropertyTypeException(value.getClass(), propertyServiceEnum);
        }
        DbProperty dbProperty = dbPropertyCrudRootServiceHelper.createDbChild();
        dbProperty.setPropertyServiceEnum(propertyServiceEnum);
        if (value instanceof Integer) {
            dbProperty.setIntegerValue((Integer) value);
        } else if (value instanceof String) {
            dbProperty.setStringValue((String) value);
        } else {
            throw new IllegalArgumentException("Unknown value: " + value);
        }
        dbPropertyCrudRootServiceHelper.updateDbChild(dbProperty);
    }

    @Override
    public CrudRootServiceHelper<DbProperty> getDPropertyCrudServiceHelper() {
        return dbPropertyCrudRootServiceHelper;
    }
}
