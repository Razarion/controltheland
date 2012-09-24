package com.btxtech.game.wicket.pages.mgmt;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.terrain.DbTerrainImage;
import com.btxtech.game.services.terrain.DbTerrainImageGroup;
import com.btxtech.game.services.terrain.TerrainImageService;
import com.btxtech.game.wicket.uiservices.CrudChildTableHelper;
import com.btxtech.game.wicket.uiservices.RuModel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.resource.ByteArrayResource;
import org.apache.wicket.spring.injection.annot.SpringBean;

import javax.swing.*;

/**
 * User: beat
 * Date: 09.11.2011
 * Time: 15:10:15
 */
public class TerrainImageGroupEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbTerrainImageGroup> serviceHelper;
    @SpringBean
    private TerrainImageService terrainImageService;

    public TerrainImageGroupEditor(DbTerrainImageGroup dbTerrainImageGroup) {
        add(new FeedbackPanel("msgs"));

        final Form<DbTerrainImageGroup> form = new Form<>("form", new CompoundPropertyModel<DbTerrainImageGroup>(new RuModel<DbTerrainImageGroup>(dbTerrainImageGroup, DbTerrainImageGroup.class) {
            @Override
            protected RuServiceHelper<DbTerrainImageGroup> getRuServiceHelper() {
                return serviceHelper;
            }
        }));
        add(form);
        form.add(new TextField("htmlBackgroundColorNone"));
        form.add(new TextField("htmlBackgroundColorWater"));
        form.add(new TextField("htmlBackgroundColorLand"));
        form.add(new TextField("htmlBackgroundColorWaterCoast"));
        form.add(new TextField("htmlBackgroundColorLandCoast"));

        new CrudChildTableHelper<DbTerrainImageGroup, DbTerrainImage>("terrainImages", null, "createTerrainImage", true, form, false) {
            @Override
            protected RuServiceHelper<DbTerrainImageGroup> getRuServiceHelper() {
                return serviceHelper;
            }

            @Override
            protected DbTerrainImageGroup getParent() {
                return form.getModelObject();
            }

            @Override
            protected CrudChildServiceHelper<DbTerrainImage> getCrudChildServiceHelperImpl() {
                return getParent().getTerrainImageCrud();
            }

            @Override
            protected void extendedPopulateItem(final Item<DbTerrainImage> item) {
                displayId(item);
                // image
                if (item.getModelObject().getImageData() != null && item.getModelObject().getImageData().length > 0) {
                    item.add(new Image("image", new ByteArrayResource("", item.getModelObject().getImageData())));
                } else {
                    item.add(new Image("image").setVisible(false));
                }
                // upload
                FileUploadField upload = new FileUploadField("upload", new IModel<FileUpload>() {
                    @Override
                    public FileUpload getObject() {
                        return null;
                    }

                    @Override
                    public void setObject(FileUpload fileUpload) {
                        ImageIcon image = new ImageIcon(fileUpload.getBytes());
                        item.getModelObject().setImageData(fileUpload.getBytes());
                        item.getModelObject().setContentType(fileUpload.getContentType());
                        item.getModelObject().setTiles((int) Math.ceil(image.getIconWidth() / Constants.TERRAIN_TILE_WIDTH),
                                (int) Math.ceil(image.getIconHeight() / Constants.TERRAIN_TILE_HEIGHT));
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
                // alternating row color
                item.add(new AttributeModifier("class", true, new Model<>(item.getIndex() % 2 == 0 ? "even" : "odd")));
            }

            @Override
            protected void onEditSubmit(DbTerrainImage dbTerrainImage) {
                setResponsePage(new TerrainImageSurfaceTypeEditor(dbTerrainImage));
            }
        };

        form.add(new Button("save") {
            @Override
            public void onSubmit() {
                serviceHelper.updateDbEntity(form.getModelObject());
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
