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
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.tutorial.ScrollToEventTaskConfig;
import com.btxtech.game.services.common.db.IndexUserType;
import com.btxtech.game.services.common.db.RectangleUserType;
import com.btxtech.game.services.item.ServerItemTypeService;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Locale;

/**
 * User: beat
 * Date: 24.07.2010
 * Time: 14:11:15
 */
@Entity
@DiscriminatorValue("SCROLL_TO_EVENT_TASK_CONFIG")
@TypeDefs({@TypeDef(name = "rectangle", typeClass = RectangleUserType.class)})
public class DbScrollToEventTaskConfig extends DbAbstractTaskConfig {
    @Type(type = "rectangle")
    @Columns(columns = {@Column(name = "xScrollToPosition"), @Column(name = "yScrollToPosition"), @Column(name = "widthScrollToPosition"), @Column(name = "heightScrollToPosition")})
    private Rectangle scrollToPosition;

    public Rectangle getScrollToPosition() {
        return scrollToPosition;
    }

    public void setScrollToPosition(Rectangle scrollToPosition) {
        this.scrollToPosition = scrollToPosition;
    }

    @Override
    protected ScrollToEventTaskConfig createTaskConfig(ServerItemTypeService serverItemTypeService, Locale locale) {
        ScrollToEventTaskConfig scrollToEventTaskConfig = new ScrollToEventTaskConfig();
        scrollToEventTaskConfig.setScrollTargetRectangle(scrollToPosition);
        return scrollToEventTaskConfig;
    }
}
