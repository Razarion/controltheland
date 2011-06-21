package com.btxtech.game.wicket.pages.mgmt.cms;

import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.DbCmsImage;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.CrudRootTableHelper;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.resource.ByteArrayResource;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 05.06.2011
 * Time: 23:32:37
 */
public class ImageRepository extends MgmtWebPage {
    @SpringBean
    private CmsService cmsService;

    public ImageRepository() {
        add(new FeedbackPanel("msgs"));

        Form form = new Form("form");
        add(form);

        new CrudRootTableHelper<DbCmsImage>("images", "saveImages", "createImage", false, form, false) {

            @Override
            protected CrudRootServiceHelper<DbCmsImage> getCrudRootServiceHelperImpl() {
                return cmsService.getImageCrudRootServiceHelper();
            }

            @Override
            protected void extendedPopulateItem(final Item<DbCmsImage> dbCmsImageItem) {
                displayId(dbCmsImageItem);
                dbCmsImageItem.add(new FileUploadField("upload", new IModel<FileUpload>() {

                    @Override
                    public FileUpload getObject() {
                        return null;
                    }

                    @Override
                    public void setObject(FileUpload fileUpload) {
                        dbCmsImageItem.getModelObject().setContentType(fileUpload.getContentType());
                        dbCmsImageItem.getModelObject().setData(fileUpload.getBytes());
                    }

                    @Override
                    public void detach() {
                    }
                }));
                dbCmsImageItem.add(new Image("image", new ByteArrayResource(dbCmsImageItem.getModelObject().getContentType(),
                        dbCmsImageItem.getModelObject().getData())));
            }
        };

        form.add(new Button("cms") {

            @Override
            public void onSubmit() {
                setResponsePage(Cms.class);
            }
        });

    }
}
