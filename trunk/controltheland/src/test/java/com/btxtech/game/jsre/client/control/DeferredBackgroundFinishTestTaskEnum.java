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

package com.btxtech.game.jsre.client.control;

import com.btxtech.game.jsre.client.control.task.AbstractStartupTask;

/**
 * User: beat
 * Date: 18.12010
 * Time: 14:18:24
 */
public enum DeferredBackgroundFinishTestTaskEnum implements StartupTaskEnum {
    TEST_1("TEST_1") {
        private DeferredStartupTestTask deferredStartupTestTask;
        @Override
        public AbstractStartupTask createTask() {
            deferredStartupTestTask = new DeferredStartupTestTask(this);
            return deferredStartupTestTask;
        }
        public DeferredStartupTestTask getTestDeferredStartupTask() {
            return deferredStartupTestTask;
        }
    },
    TEST_2_BACKGROUND("TEST_2_BACKGROUND") {
        private DeferredStartupTestTask deferredStartupTestTask;
        @Override
        public AbstractStartupTask createTask() {
            deferredStartupTestTask = new DeferredBackgroundStartupTestTask(this);
            return deferredStartupTestTask;
        }
        public DeferredStartupTestTask getTestDeferredStartupTask() {
            return deferredStartupTestTask;
        }
    },
    TEST_3_DEFERRED_BACKGROUND_FINISH("TEST_3_DEFERRED_BACKGROUND_FINISH") {
        @Override
        public AbstractStartupTask createTask() {
            return new DeferredBackgroundFinishStartupTestTask(this);
        }
    },
    TEST_4_DEFERRED_FINISH("TEST_4_DEFERRED_FINISH") {
        @Override
        public AbstractStartupTask createTask() {
            return new DeferredFinishStartupTestTask(this);
        }
    };

    private StartupTaskEnumHtmlHelper startupTaskEnumHtmlHelper;

    public DeferredStartupTestTask getTestDeferredStartupTask() {
        return null;
    }

    DeferredBackgroundFinishTestTaskEnum(String niceText) {
        startupTaskEnumHtmlHelper = new StartupTaskEnumHtmlHelper(niceText, this);
    }

    @Override
    public boolean isFirstTask() {
        return ordinal() == 0;
    }

    @Override
    public StartupTaskEnumHtmlHelper getStartupTaskEnumHtmlHelper() {
        return startupTaskEnumHtmlHelper;
    }
}
