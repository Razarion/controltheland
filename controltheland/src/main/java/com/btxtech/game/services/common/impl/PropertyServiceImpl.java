package com.btxtech.game.services.common.impl;

import com.btxtech.game.services.common.CrudRootServiceHelper;
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
        // TODO handle other types
        dbProperty.setIntegerValue((Integer) value);
        dbPropertyCrudRootServiceHelper.updateDbChild(dbProperty);
    }

    @Override
    public CrudRootServiceHelper<DbProperty> getDPropertyCrudServiceHelper() {
        return dbPropertyCrudRootServiceHelper;
    }
}
