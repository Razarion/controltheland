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
import com.btxtech.game.jsre.common.tutorial.TerrainPositionSpeechBubbleHintConfig;
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
@DiscriminatorValue("TERRAIN_SPEECH_BUBBLE")
@TypeDef(name = "index", typeClass = IndexUserType.class)
public class DbTerrainPositionSpeechBubbleHintConfig extends DbHintConfig {
    @Type(type = "index")
    @Columns(columns = {@Column(name = "xPos"), @Column(name = "yPos")})
    private Index position;
    @Column(length = 50000)
    private String html;

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public Index getPosition() {
        return position;
    }

    public void setPosition(Index position) {
        this.position = position;
    }

    @Override
    public void init() {
        position = new Index(0, 0);
    }

    @Override
    public HintConfig createHintConfig(ResourceHintManager resourceHintManager) {
        return new TerrainPositionSpeechBubbleHintConfig(position, html);
    }
}
