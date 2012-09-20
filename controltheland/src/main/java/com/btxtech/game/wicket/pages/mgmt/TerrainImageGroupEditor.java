package com.btxtech.game.wicket.pages.mgmt;

import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.terrain.DbTerrainImageGroup;
import com.btxtech.game.services.terrain.TerrainImageService;
import org.apache.wicket.spring.injection.annot.SpringBean;

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
        // TODO make per planet  tile width and height global
        throw new UnsupportedOperationException();
/*
        add(new FeedbackPanel("msgs"));

        final Form<DbTerrainImageGroup> form = new Form<DbTerrainImageGroup>("form", new CompoundPropertyModel<DbTerrainImageGroup>(new RuModel<DbTerrainImageGroup>(dbTerrainImageGroup, DbTerrainImageGroup.class) {
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
                        item.getModelObject().setTiles((int) Math.ceil(image.getIconWidth() / terrainImageService.getTerrainSettings().getTileWidth()),
                                (int) Math.ceil(image.getIconHeight() / terrainImageService.getTerrainSettings().getTileHeight()));
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
                item.add(new AttributeModifier("class", true, new Model<String>(item.getIndex() % 2 == 0 ? "even" : "odd")));
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
        */
    }
}
