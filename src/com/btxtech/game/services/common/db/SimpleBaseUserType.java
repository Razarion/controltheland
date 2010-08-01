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

import com.btxtech.game.jsre.common.SimpleBase;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * User: beat
 * Date: July 24, 2009
 * Time: 11:59:21 AM
 */
public class SimpleBaseUserType implements UserType {
    @Override
    public int[] sqlTypes() {
        // Name, Color, Bot
        return new int[]{Types.VARCHAR, Types.VARCHAR, Types.BIT};
    }

    @Override
    public Class returnedClass() {
        return SimpleBase.class;
    }

    @Override
    public boolean equals(Object o1, Object o2) throws HibernateException {
        return o1 == o2 || !(o1 == null || o2 == null) && o1.equals(o2);
    }

    @Override
    public int hashCode(Object o) throws HibernateException {
        return o.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String[] names, Object owner) throws HibernateException, SQLException {
        String name = resultSet.getString(names[0]);
        if (resultSet.wasNull()) {
            return null;
        }

        String color = resultSet.getString(names[1]);
        if (resultSet.wasNull()) {
            return null;
        }

        boolean isBot = resultSet.getBoolean(names[2]);
        if (resultSet.wasNull()) {
            return null;
        }

        return new SimpleBase(name, color, isBot);
    }

    @Override
    public void nullSafeSet(PreparedStatement statement, Object o, int columnIndex) throws HibernateException, SQLException {
        SimpleBase simpleBase = (SimpleBase) o;
        if (simpleBase != null) {
            statement.setString(columnIndex, simpleBase.getName());
            statement.setString(columnIndex + 1, simpleBase.getHtmlColor());
            statement.setBoolean(columnIndex + 2, simpleBase.isBot());
        } else {
            statement.setNull(columnIndex, Types.VARCHAR);
            statement.setNull(columnIndex + 1, Types.VARCHAR);
            statement.setNull(columnIndex + 2, Types.BIT);
        }
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
        throw new NotImplementedException();
    }

    @Override
    public Object assemble(Serializable serializable, Object o) throws HibernateException {
        throw new NotImplementedException();
    }

    @Override
    public Object replace(Object o, Object o1, Object o2) throws HibernateException {
        throw new NotImplementedException();
    }
}