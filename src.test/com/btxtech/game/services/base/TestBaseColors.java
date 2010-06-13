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

package com.btxtech.game.services.base;

import com.btxtech.game.services.base.impl.BaseServiceImpl;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * User: beat
 * Date: Oct 3, 2009
 * Time: 10:39:24 AM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:war/WEB-INF/applicationContext.xml"})
public class TestBaseColors {
    @Autowired
    private BaseService baseService;

    @Test
    public void testFillBaseColor() {
        ((BaseServiceImpl)baseService).fillBaseColors();
    }

    @Test
    public void testGetBaseColor() {
        baseService.getFreeColors(15);
    }
    
}
