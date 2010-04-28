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

package com.btxtech.game.services.mgmt;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.services.common.db.RectangleUserType;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

/**
 * User: beat
 * Date: 31.03.2010
 * Time: 13:54:45
 */
@Entity(name = "MGMT_STARTUP")
@TypeDef(name = "rectangle", typeClass = RectangleUserType.class)
public class StartupData implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    private int startMoney;
    private int tutorialTimeout;
    private int registerDialogDelay;
    private int userActionCollectionTime;
    @Type(type = "rectangle")
    @Columns(columns = {@Column(name = "startRectX"), @Column(name = "startRectY"), @Column(name = "startRectEndX"), @Column(name = "startRectEndY")})
    private Rectangle startRectangle;
    private static Log log = LogFactory.getLog(StartupData.class);

    public int getStartMoney() {
        return startMoney;
    }

    public void setStartMoney(int startMoney) {
        this.startMoney = startMoney;
    }

    public int getTutorialTimeout() {
        return tutorialTimeout;
    }

    public void setTutorialTimeout(int tutorialTimeout) {
        this.tutorialTimeout = tutorialTimeout;
    }

    public int getRegisterDialogDelay() {
        return registerDialogDelay;
    }

    public void setRegisterDialogDelay(int registerDialogDelay) {
        this.registerDialogDelay = registerDialogDelay;
    }

    public int getUserActionCollectionTime() {
        return userActionCollectionTime;
    }

    public void setUserActionCollectionTime(int userActionCollectionTime) {
        this.userActionCollectionTime = userActionCollectionTime;
    }

    public Rectangle getStartRectangle() {
        if (startRectangle == null) {
            log.info("StartRectangle is null. Return a faked one.");
            startRectangle = new Rectangle(0, 0, 1000, 1000);
        }
        return startRectangle;
    }

    public void setStartRectangle(Rectangle startRectangle) {
        this.startRectangle = startRectangle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StartupData that = (StartupData) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
