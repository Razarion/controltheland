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
public enum TestSimpleExceptionTaskEnum implements StartupTaskEnum {
    TEST_1("TEST_1") {
        @Override
        public AbstractStartupTask createTask() {
            return new TestSimpleStartupTask(this);
        }},
    TEST_2_EXCEPTION("TEST_2_EXCEPTION") {
        @Override
        public AbstractStartupTask createTask() {
            return new TestSimpleExceptionStartupTask(this);
        }},
    TEST_3("TEST_3") {
        @Override
        public AbstractStartupTask createTask() {
            return new TestSimpleStartupTask(this);
        }};


    private StartupTaskEnumHtmlHelper startupTaskEnumHtmlHelper;

    TestSimpleExceptionTaskEnum(String niceText) {
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
