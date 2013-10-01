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

package com.btxtech.game.wicket.uiservices;

import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import org.apache.wicket.markup.html.WebMarkupContainer;

import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 23.07.2010
 * Time: 23:48:39
 */
public abstract class CrudRootTableHelper<T extends CrudChild> extends AbstractCrudRootTableHelper<T> {

    public CrudRootTableHelper(String tableId, String saveId, String createId, final boolean showEdit, WebMarkupContainer markupContainer, final boolean showOrderButtons) {
        super(tableId, saveId, createId, showEdit, markupContainer, showOrderButtons);
    }

    protected CrudRootServiceHelper<T> getCrudRootServiceHelperImpl() {
        return null;
    }

    @Override
    protected Collection<T> readDbChildren() {
        return getCrudRootServiceHelperImpl().readDbChildren();
    }

    @Override
    protected void deleteChild(T modelObject) {
        getCrudRootServiceHelperImpl().deleteDbChild(modelObject);
    }

    @Override
    protected void updateDbChildren(List<T> children) {
        getCrudRootServiceHelperImpl().updateDbChildren(children);
    }

    @Override
    protected T createDbChild() {
        return getCrudRootServiceHelperImpl().createDbChild();
    }

    @Override
    protected <C extends T> C createDbChild(Class<C> createClass) {
        return (C) getCrudRootServiceHelperImpl().createDbChild(createClass);
    }

}
