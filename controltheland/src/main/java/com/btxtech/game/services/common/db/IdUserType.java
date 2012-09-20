/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.services.common.db;

import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;


/**
 * User: beat
 * Date: Sep 23, 2009
 * Time: 11:00:21 AM
 */
public class IdUserType implements UserType {
    @Override
    public int[] sqlTypes() {
        return new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER};
    }

    @Override
    public Class returnedClass() {
        return Id.class;
    }

    @Override
    public boolean equals(Object o1, Object o2) throws HibernateException {
        if (o1 == o2) {
            return true;
        } else if (o1 == null || o2 == null) {
            return false;
        } else {
            return o1.equals(o2);
        }
    }

    @Override
    public int hashCode(Object o) throws HibernateException {
        return o.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String[] names, SessionImplementor sessionImplementor, Object owner) throws HibernateException, SQLException {
        int id = resultSet.getInt(names[0]);
        boolean hasId = !resultSet.wasNull();

        int parentId = resultSet.getInt(names[1]);
        int childIndex = resultSet.getInt(names[2]);
        if (hasId) {
            return new Id(id, parentId, childIndex);
        } else {
            return new Id(parentId, childIndex);
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement statement, Object o, int columnIndex, SessionImplementor sessionImplementor) throws HibernateException, SQLException {
        Id id = (Id) o;
        if (id.isSynchronized()) {
            statement.setInt(columnIndex, id.getId());
        } else {
            statement.setNull(columnIndex, Types.INTEGER);
        }
        statement.setInt(columnIndex + 1, id.getParentId());
        statement.setInt(columnIndex + 2, id.getChildIndex());
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object o) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object assemble(Serializable serializable, Object o) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object replace(Object o, Object o1, Object o2) throws HibernateException {
        throw new UnsupportedOperationException();
    }
}
