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
import com.btxtech.game.services.common.CrudServiceHelper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;

/**
 * User: beat
 * Date: 23.07.2010
 * Time: 23:48:39
 */
public abstract class CrudTableHelper<T extends CrudChild> implements Serializable {
    public static final String NAME = "name";
    private ListProvider<T> provider;

    public CrudTableHelper(String tableId, String saveId, String createId, final boolean showEdit, Form form) {
        provider = new ListProvider<T>() {
            @Override
            protected List<T> createList() {
                Collection<T> collection = getCrudServiceHelper().readDbChildren();
                if (collection instanceof List) {
                    return (List<T>) collection;
                } else {
                    return new ArrayList<T>(collection);
                }
            }
        };
        form.add(new DataView<T>(tableId, provider) {
            @Override
            protected void populateItem(final Item<T> item) {
                extendedPopulateItem(item);
                if (showEdit) {
                    item.add(new Button("edit") {

                        @Override
                        public void onSubmit() {
                            onEditSubmit(item.getModelObject());
                        }
                    });
                }

                item.add(new Button("delete") {

                    @Override
                    public void onSubmit() {
                        deleteChild(item.getModelObject());
                    }
                });

            }
        });

        if (saveId != null) {
            setupSave(form, saveId);
        }
        setupCreate(form, createId);

    }

    protected void deleteChild(T child) {
        getCrudServiceHelper().deleteDbChild(child);
    }

    protected void setupSave(Form form, String saveId) {
        form.add(new Button(saveId) {

            @Override
            public void onSubmit() {
                getCrudServiceHelper().updateDbChildren(getLastModifiedList());
            }
        });
    }

    protected void setupCreate(Form form, String createId) {
        form.add(new Button(createId) {

            @Override
            public void onSubmit() {
                getCrudServiceHelper().createDbChild();
            }
        });
    }

    /**
     * Overide in subclasses
     *
     * @param item From PopulateItem
     */
    protected void extendedPopulateItem(Item<T> item) {
        item.add(new TextField<String>("name"));
    }

    abstract protected CrudServiceHelper<T> getCrudServiceHelper();

    /**
     * Overide in subclasses
     *
     * @param t the item to edit
     */
    protected void onEditSubmit(T t) {

    }

    public void swapRow(int i, int j) {
        Collections.swap(provider.getLastModifiedList(), i, j);
        getCrudServiceHelper().updateDbChildren(provider.getLastModifiedList());
    }

    public int rowCount() {
        return provider.getLastModifiedList().size();
    }

    public List<T> getLastModifiedList() {
        return provider.getLastModifiedList();
    }

}
