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
public enum DeferredTestTaskEnum implements StartupTaskEnum {
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

        @Override
        public String getI18nText() {
            throw new UnsupportedOperationException();
        }
    },
    TEST_2("TEST_2") {
        private DeferredStartupTestTask deferredStartupTestTask;

        @Override
        public AbstractStartupTask createTask() {
            deferredStartupTestTask = new DeferredStartupTestTask(this);
            return deferredStartupTestTask;
        }

        public DeferredStartupTestTask getTestDeferredStartupTask() {
            return deferredStartupTestTask;
        }

        @Override
        public String getI18nText() {
            throw new UnsupportedOperationException();
        }
    },
    TEST_3("TEST_3") {
        private DeferredStartupTestTask deferredStartupTestTask;

        @Override
        public AbstractStartupTask createTask() {
            deferredStartupTestTask = new DeferredStartupTestTask(this);
            return deferredStartupTestTask;
        }

        public DeferredStartupTestTask getTestDeferredStartupTask() {
            return deferredStartupTestTask;
        }

        @Override
        public String getI18nText() {
            throw new UnsupportedOperationException();
        }
    };

    private StartupTaskEnumHtmlHelper startupTaskEnumHtmlHelper;

    public abstract DeferredStartupTestTask getTestDeferredStartupTask();

    DeferredTestTaskEnum(String niceText) {
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
