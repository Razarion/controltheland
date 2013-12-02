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
import com.btxtech.game.jsre.common.tutorial.AutomatedScrollTaskConfig;
import com.btxtech.game.services.item.ServerItemTypeService;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;

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
@DiscriminatorValue("AUTOMATED_SCROLL_TASK_CONFIG")
public class DbAutomatedScrollTaskConfig extends DbAbstractTaskConfig {
    @Type(type = "index")
    @Columns(columns = {@Column(name = "automatedScrollToPositionX"), @Column(name = "automatedScrollToPositionY")})
    private Index automatedScrollToPosition;

    public Index getAutomatedScrollToPosition() {
        return automatedScrollToPosition;
    }

    public void setAutomatedScrollToPosition(Index automatedScrollToPosition) {
        this.automatedScrollToPosition = automatedScrollToPosition;
    }

    @Override
    protected AutomatedScrollTaskConfig createTaskConfig(ServerItemTypeService serverItemTypeService, Locale locale) {
        AutomatedScrollTaskConfig automatedScrollTaskConfig = new AutomatedScrollTaskConfig();
        automatedScrollTaskConfig.setScrollToPosition(automatedScrollToPosition);
        return automatedScrollTaskConfig;
    }
}
