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

package com.btxtech.game.services;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.GenericWebApplicationContext;


public class TestWebSessionContextLoader extends TestWebContextLoader {

	@Override
	protected void customizeContext(final GenericWebApplicationContext context) {
		MockServletContext servlet = new MockServletContext();
		context.setServletContext(servlet);

		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpSession session = new MockHttpSession();
		request.setSession(session);
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		context.refresh();
		//context.getBeanFactory().registerScope("session", new SessionScope());
        context.getBeanFactory().registerScope(WebApplicationContext.SCOPE_SESSION, new SimpleMapScope(context));
        //context.getBeanFactory().registerScope(WebApplicationContext.SCOPE_REQUEST, new SimpleMapScope(context));
	}
}