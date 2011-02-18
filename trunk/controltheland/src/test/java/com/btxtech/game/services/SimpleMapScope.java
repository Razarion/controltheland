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

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

public class SimpleMapScope implements Scope {
    private static SimpleMapScope instance;
    private ScopeType scopeType = ScopeType.THREAD;
    private String manuelScopeId;

    public enum ScopeType {
        THREAD,
        MANUEL
    }

    private final Map<Object, Object> objectMap = new HashMap<Object, Object>();
    private GenericWebApplicationContext applicationContext;

    public SimpleMapScope(GenericWebApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        instance = this;
    }

    /**
     * {@inheritDoc}
     */
    public Object get(final String theName, final ObjectFactory theObjectFactory) {
        Object id;
        switch (scopeType) {
            case MANUEL:
                id = manuelScopeId;
                break;
            case THREAD:
                id = Thread.currentThread();
                break;
            default:
                throw new IllegalStateException("Unknwon scope type " + scopeType);
        }

        Object object = objectMap.get(id);
        if (null == object) {
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpSession session = new MockHttpSession();
            request.setSession(session);
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
            object = theObjectFactory.getObject();
            objectMap.put(id, object);
        }
        return object;
    }

    /**
     * {@inheritDoc}
     */
    public String getConversationId() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void registerDestructionCallback(final String theName, final Runnable theCallback) {
        // nothing to do ... this is optional and not required
    }

    /**
     * {@inheritDoc}
     */
    public Object remove(final String theName) {
        return objectMap.remove(theName);
    }

    public void setManuelScopeId(String manuelScopeId) {
        this.manuelScopeId = manuelScopeId;
    }

    public void setScopeType(ScopeType scopeType) {
        this.scopeType = scopeType;
    }

    public static SimpleMapScope getInstance() {
        return instance;
    }
}
