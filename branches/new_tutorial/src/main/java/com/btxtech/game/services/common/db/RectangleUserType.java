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

import com.btxtech.game.jsre.client.common.Rectangle;
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
 * Date: April 28, 2009
 * Time: 10:12:18 AM
 */
public class RectangleUserType implements UserType {
    @Override
    public int[] sqlTypes() {
        return new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER};
    }

    @Override
    public Class returnedClass() {
        return Rectangle.class;
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
    public Object nullSafeGet(ResultSet resultSet, String[] names, SessionImplementor sessionImplementor, Object owner) throws HibernateException, SQLException {
        int x = resultSet.getInt(names[0]);
        if (resultSet.wasNull()) {
            return null;
        }
        int y = resultSet.getInt(names[1]);
        if (resultSet.wasNull()) {
            return null;
        }
        int width = resultSet.getInt(names[2]);
        if (resultSet.wasNull()) {
            return null;
        }
        int height = resultSet.getInt(names[3]);
        if (resultSet.wasNull()) {
            return null;
        }

        return new Rectangle(x, y, width, height);
    }

    @Override
    public void nullSafeSet(PreparedStatement statement, Object obj, int columnIndex, SessionImplementor sessionImplementor) throws HibernateException, SQLException {
        Rectangle rectangle = (Rectangle) obj;
        if (rectangle != null) {
            statement.setInt(columnIndex, rectangle.getX());
            statement.setInt(columnIndex + 1, rectangle.getY());
            statement.setInt(columnIndex + 2, rectangle.getWidth());
            statement.setInt(columnIndex + 3, rectangle.getHeight());
        } else {
            statement.setNull(columnIndex, Types.INTEGER);
            statement.setNull(columnIndex + 1, Types.INTEGER);
            statement.setNull(columnIndex + 2, Types.INTEGER);
            statement.setNull(columnIndex + 3, Types.INTEGER);
        }
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        if (value != null) {
            return ((Rectangle) value).copy();
        } else {
            return null;
        }
    }

    @Override
    public boolean isMutable() {
        return true;
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
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return ((Rectangle) original).copy();
    }
}