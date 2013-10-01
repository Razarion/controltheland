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

import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.terrain.DbScatterSurfaceImage;
import com.btxtech.game.services.terrain.DbSurfaceImage;
import com.btxtech.game.services.terrain.TerrainImageService;
import com.btxtech.game.wicket.uiservices.CrudChildTableHelper;
import com.btxtech.game.wicket.uiservices.PercentPanel;
import com.btxtech.game.wicket.uiservices.RuModel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * User: beat
 * Date: Sep 24, 2013
 * Time: 10:35:35 PM
 */
public class ScatterSurfaceImageEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbSurfaceImage> ruServiceHelper;
    @SpringBean
    private TerrainImageService terrainImageService;

    public ScatterSurfaceImageEditor(DbSurfaceImage dbSurfaceImage) {
        add(new FeedbackPanel("msgs"));

        final Form<DbSurfaceImage> form = new Form<>("form", new CompoundPropertyModel<>(new RuModel<DbSurfaceImage>(dbSurfaceImage, DbSurfaceImage.class) {
            @Override
            protected RuServiceHelper<DbSurfaceImage> getRuServiceHelper() {
                return ruServiceHelper;
            }
        }));
        add(form);

        form.add(new Label("common", new AbstractReadOnlyModel<Integer>() {
            @Override
            public Integer getObject() {
                return (int) ((1.0 - form.getModelObject().getUncommon() - form.getModelObject().getRare()) * 100.0);
            }
        }));
        form.add(new PercentPanel("uncommon"));
        form.add(new PercentPanel("rare"));

        new CrudChildTableHelper<DbSurfaceImage, DbScatterSurfaceImage>("scatterSurfaceImages", "updateScatterSurfaceImages", "createScatterSurfaceImage", false, form, false) {
            @Override
            protected void extendedPopulateItem(final Item<DbScatterSurfaceImage> item) {
                // Id
                displayId(item);
                // Frequency
                item.add(new DropDownChoice<>("frequency", Arrays.asList(DbScatterSurfaceImage.Frequency.values())));
                // Image
                if (item.getModelObject().getImageData() != null && item.getModelObject().getImageData().length > 0) {
                    item.add(new Image("image", new ByteArrayResource("", item.getModelObject().getImageData())));
                } else {
                    item.add(new Image("image", "").setVisible(false));
                }
                // Upload
                FileUploadField upload = new FileUploadField("upload", new IModel<List<FileUpload>>() {
                    @Override
                    public List<FileUpload> getObject() {
                        return null;
                    }

                    @Override
                    public void setObject(List<FileUpload> list) {
                        if (list == null) {
                            // Don't know why
                            return;
                        }
                        // TODO check size
                        item.getModelObject().setImageData(list.get(0).getBytes());
                        item.getModelObject().setContentType(list.get(0).getContentType());
                    }

                    @Override
                    public void detach() {
                        //Ignored
                    }
                });
                item.add(upload);
                // Size
                double size = item.getModelObject().getImageData() != null ? item.getModelObject().getImageData().length / 1000.0 : 0;
                item.add(new Label("size", Double.toString(size)));
            }

            @Override
            protected RuServiceHelper<DbSurfaceImage> getRuServiceHelper() {
                return ruServiceHelper;
            }

            @Override
            protected DbSurfaceImage getParent() {
                return (DbSurfaceImage) form.getDefaultModelObject();
            }

            @Override
            protected CrudChildServiceHelper<DbScatterSurfaceImage> getCrudChildServiceHelperImpl() {
                return ((DbSurfaceImage) form.getDefaultModelObject()).getScatterSurfaceImageCrudHelper();
            }

            @Override
            protected List<DbScatterSurfaceImage> sortList(List<DbScatterSurfaceImage> list) {
                List<DbScatterSurfaceImage> surfaceImages = new ArrayList<>(list);
                Collections.sort(surfaceImages);
                return surfaceImages;
            }
        };

        form.add(new Button("tileEditor") {
            @Override
            public void onSubmit() {
                setResponsePage(new TerrainTileEditor());
            }
        });
        form.add(new Button("activate") {
            @Override
            public void onSubmit() {
                terrainImageService.activate();
            }
        });
    }
}
