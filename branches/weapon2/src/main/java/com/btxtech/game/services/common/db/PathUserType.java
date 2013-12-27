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

import com.btxtech.game.jsre.client.common.Index;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * User: beat
 * Date: Sep 23, 2009
 * Time: 11:37:48 AM
 */
public class PathUserType implements UserType {
    public static final int MAX_STRING_LENGTH = 1000;
    private static final String X_Y_DELIMITER = ",";
    private static final String DELIMITER = ";";
    private Log log = LogFactory.getLog(PathUserType.class);

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.VARCHAR};
    }

    @Override
    public Class returnedClass() {
        return List.class;
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
        String string = resultSet.getString(names[0]);
        if (resultSet.wasNull()) {
            return null;
        }
        ArrayList<Index> path = new ArrayList<Index>();
        StringTokenizer st = new StringTokenizer(string, DELIMITER);
        while (st.hasMoreTokens()) {
            String posString = st.nextToken();
            int delimiterPos = posString.indexOf(X_Y_DELIMITER);
            String xStr = posString.substring(0, delimiterPos);
            String yStr = posString.substring(delimiterPos + 1, posString.length());
            Index index = new Index(Integer.parseInt(xStr), Integer.parseInt(yStr));
            path.add(index);
        }
        return path;
    }

    @Override
    public void nullSafeSet(PreparedStatement statement, Object obj, int columnIndex, SessionImplementor sessionImplementor) throws HibernateException, SQLException {
        List<Index> path = (List<Index>) obj;
        if (path == null || path.isEmpty()) {
            statement.setNull(columnIndex, Types.VARCHAR);
            return;
        }

        StringBuilder builder = new StringBuilder();
        path = new ArrayList<>(path); // Prevent concurrent modification exception
        for (Index index : path) {
            builder.append(index.getX());
            builder.append(X_Y_DELIMITER);
            builder.append(index.getY());
            builder.append(DELIMITER);
        }
        if (builder.length() > MAX_STRING_LENGTH) {
            log.error("Path is to long in PathUserType.nullSafeSet(). Path will not be saved.");
            statement.setNull(columnIndex, Types.VARCHAR);
        } else {
            statement.setString(columnIndex, builder.toString());
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
