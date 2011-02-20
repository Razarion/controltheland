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


/**
 * User: beat
 * Date: 18.02.2011
 * Time: 14:35:33
 */
public enum TestStartupSeq implements StartupSeq {
    TEST_SIMPLE {
        @Override
        public StartupTaskEnum[] getAbstractStartupTaskEnum() {
            return TestSimpleTaskEnum.values();
        }
        @Override
        public boolean isCold() {
            return true;
        }
    },
    TEST_DEFERRED {
        @Override
        public StartupTaskEnum[] getAbstractStartupTaskEnum() {
            return TestDeferredTaskEnum.values();
        }
        @Override
        public boolean isCold() {
            return true;
        }
    },
    TEST_BACKGROUND {
        @Override
        public StartupTaskEnum[] getAbstractStartupTaskEnum() {
            return TestDeferredBackgroundTaskEnum.values();
        }
        @Override
        public boolean isCold() {
            return true;
        }},
    TEST_DEFERRED_FINISH {
        @Override
        public StartupTaskEnum[] getAbstractStartupTaskEnum() {
            return TestDeferredFinishTaskEnum.values();
        }
        @Override
        public boolean isCold() {
            return true;
        }
    },
    TEST_DEFERRED_BACKGROUND_FINISH {
        @Override
        public StartupTaskEnum[] getAbstractStartupTaskEnum() {
            return TestDeferredBackgroundFinishTaskEnum.values();
        }
        @Override
        public boolean isCold() {
            return true;
        }
    },
    TEST_SIMPLE_EXCEPTION {
        @Override
        public StartupTaskEnum[] getAbstractStartupTaskEnum() {
            return TestSimpleExceptionTaskEnum.values();
        }
        @Override
        public boolean isCold() {
            return true;
        }
    };


    @Override
    public boolean isBackEndMode() {
        return true;
    }
}
