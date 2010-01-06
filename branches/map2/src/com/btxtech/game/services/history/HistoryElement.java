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

package com.btxtech.game.services.history;

import com.btxtech.game.jsre.common.SimpleBase;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * User: beat
 * Date: Jul 5, 2009
 * Time: 7:28:46 PM
 */
@Entity
@Table(name="HISTORY")
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="HISTORY_TYPE", discriminatorType= DiscriminatorType.STRING)
abstract public class HistoryElement implements Serializable{
    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false)
    private String user;
    @Column(nullable = false)
    private Date timeStamp;

    /**
     * Used by hibernate
     */
    protected HistoryElement() {
    }

    public HistoryElement(SimpleBase simpleBase) {
        user = simpleBase.getName();
        timeStamp = new Date();
    }

    public String getUser() {
        return user;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    abstract public String getMessage();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HistoryElement that = (HistoryElement) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
