package com.btxtech.game.wicket.pages.mgmt;

import com.btxtech.game.jsre.client.SoundHandler;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.mapeditor.TerrainEditorAsync;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.media.ClipService;
import com.btxtech.game.services.media.DbImageSpriteMap;
import com.btxtech.game.services.media.DbSound;
import com.btxtech.game.services.media.SoundService;
import com.btxtech.game.wicket.uiservices.CrudRootTableHelper;

import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 13.08.2012
 * Time: 18:38:58
 */
public class ImageSpriteMapLibrary extends MgmtWebPage {
    @SpringBean
    private ClipService clipService;

    public ImageSpriteMapLibrary() {
        add(new FeedbackPanel("msgs"));

        Form form = new Form("form"); 
        add(form);

        new CrudRootTableHelper<DbImageSpriteMap>("imageSpriteMaps", "saveImageSpriteMaps", "createImageSpriteMap", true, form, false) {

            @Override
            protected void extendedPopulateItem(final Item<DbImageSpriteMap> dbimageSpriteMap) {
                displayId(dbimageSpriteMap);
                super.extendedPopulateItem(dbimageSpriteMap);
            }

            @Override
            protected CrudRootServiceHelper<DbImageSpriteMap> getCrudRootServiceHelperImpl() {
                return clipService.getImageSpriteMapCrud();
            }

            @Override
            protected void onEditSubmit(DbImageSpriteMap dbImageSpriteMap) {
                PageParameters pageParameters =new PageParameters();
                pageParameters.add(TerrainEditorAsync.REGION_ID, Integer.toString(dbImageSpriteMap.getId())); // TODO
                setResponsePage(ImageSpriteMapEditor.class, pageParameters);
            }
        };
    }
}