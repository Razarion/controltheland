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

package com.btxtech.game.wicket.pages.mgmt;

import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbResourceItemType;
import com.btxtech.game.services.resource.DbRegionResource;
import com.btxtech.game.services.resource.ResourceService;
import java.util.Iterator;
import java.util.List;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 09.05.2010
 * Time: 15:49:16
 */
public class ResourceEditor extends WebPage {
    @SpringBean
    private ResourceService resourceService;
    @SpringBean
    private ItemService itemService;
    private List<DbRegionResource> modifiedDbRegionResources;

    public ResourceEditor() {
        add(new FeedbackPanel("msgs"));
        Form form = new Form("regionResourceForm");
        add(form);
        final RegionResourceProvider regionResourceProvider = new RegionResourceProvider();
        form.add(new DataView<DbRegionResource>("regionResources", regionResourceProvider) {

            @Override
            protected void populateItem(final Item<DbRegionResource> item) {
                item.add(new TextField<String>("name"));
                item.add(new TextField<String>("count"));
                item.add(new TextField<Integer>("resourceItemType", new IModel<Integer>() {

                    @Override
                    public Integer getObject() {
                        DbResourceItemType dbResourceItemType = item.getModelObject().getResourceItemType();
                        if (dbResourceItemType != null) {
                            return dbResourceItemType.getId();
                        } else {
                            return null;
                        }
                    }

                    @Override
                    public void setObject(Integer id) {
                        if (id != null) {
                            item.getModelObject().setResourceItemType((DbResourceItemType) itemService.getDbItemType(id));
                        } else {
                            item.getModelObject().setResourceItemType(null);
                        }
                    }

                    @Override
                    public void detach() {
                        //Ignore 
                    }
                }, Integer.class));
                item.add(new TextField<String>("region.x"));
                item.add(new TextField<String>("region.y"));
                item.add(new TextField<String>("region.width"));
                item.add(new TextField<String>("region.height"));
                item.add(new TextField<String>("minDistanceToItems"));

                item.add(new Button("delete") {

                    @Override
                    public void onSubmit() {
                        resourceService.deleteDbRegionResource(item.getModelObject());
                    }
                });
            }
        });
        form.add(new Button("addRegionResource") {

            @Override
            public void onSubmit() {
                resourceService.addDbRegionResource();
            }
        });
        form.add(new Button("saveRegionResource") {

            @Override
            public void onSubmit() {
                if (modifiedDbRegionResources == null) {
                    throw new NullPointerException();
                }
                resourceService.saveDbRegionResource(modifiedDbRegionResources);
            }
        });
        form.add(new Button("activateRegionResource") {

            @Override
            public void onSubmit() {
                resourceService.resetAllResources();
            }
        });

    }

    class RegionResourceProvider implements IDataProvider<DbRegionResource> {
        private List<DbRegionResource> dbRegionResources;

        @Override
        public Iterator<? extends DbRegionResource> iterator(int first, int count) {
            return getDbRegionResource().subList(first, first + count).iterator();
        }

        @Override
        public int size() {
            return getDbRegionResource().size();
        }

        @Override
        public IModel<DbRegionResource> model(DbRegionResource dbRegionResource) {
            return new CompoundPropertyModel<DbRegionResource>(dbRegionResource);
        }

        @Override
        public void detach() {
            dbRegionResources = null;
        }

        public List<DbRegionResource> getDbRegionResource() {
            if (dbRegionResources == null) {
                dbRegionResources = resourceService.getDbRegionResources();
                modifiedDbRegionResources = dbRegionResources;
            }
            return dbRegionResources;
        }
    }
}
