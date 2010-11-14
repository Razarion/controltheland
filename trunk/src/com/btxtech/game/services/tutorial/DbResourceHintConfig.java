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

package com.btxtech.game.services.tutorial;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.tutorial.HintConfig;
import com.btxtech.game.jsre.common.tutorial.ResourceHintConfig;
import com.btxtech.game.services.common.db.IndexUserType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

/**
 * User: beat
 * Date: 27.07.2010
 * Time: 19:19:28
 */
@Entity
@DiscriminatorValue("RESOURCE")
@TypeDef(name = "index", typeClass = IndexUserType.class)
public class DbResourceHintConfig extends DbHintConfig {
    @Type(type = "index")
    @Columns(columns = {@Column(name = "xPos"), @Column(name = "yPos")})
    private Index position;
    private String contentType;
    @Column(length = 500000)
    private byte[] data;

    public static DbResourceHintConfig createImageOnly(String contentType, byte[] data) {
        DbResourceHintConfig dbResourceHintConfig = new DbResourceHintConfig();
        dbResourceHintConfig.setData(data);
        dbResourceHintConfig.setContentType(contentType);
        return dbResourceHintConfig;
    }

    public Index getPosition() {
        return position;
    }

    public void setPosition(Index position) {
        this.position = position;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public void init() {
        position = new Index(0, 0);
    }

    @Override
    public HintConfig createHintConfig(ResourceHintManager resourceHintManager) {
        if (data == null || data.length == 0 || contentType == null) {
            return null;
        }
        int id = resourceHintManager.addResource(this);
        return new ResourceHintConfig(isCloseOnTaskEnd(), position, id);
    }
}
