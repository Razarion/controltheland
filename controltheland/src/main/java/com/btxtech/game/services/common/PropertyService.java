package com.btxtech.game.services.common;

import com.btxtech.game.services.common.db.DbProperty;

/**
 * User: beat
 * Date: 04.06.13
 * Time: 18:22
 */
public interface PropertyService {
    public int getIntProperty(PropertyServiceEnum propertyServiceEnum) throws NoSuchPropertyException, WrongPropertyTypeException;

    void createProperty(PropertyServiceEnum propertyServiceEnum, Object value) throws WrongPropertyTypeException;

    CrudRootServiceHelper<DbProperty> getDPropertyCrudServiceHelper();
}
