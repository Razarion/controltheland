package com.btxtech.game.wicket.pages.mgmt;

import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.common.info.PreloadedImageSpriteMapInfo;
import com.btxtech.game.jsre.imagespritemapeditor.ImageSpriteMapAccessAsync;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.media.ClipService;
import com.btxtech.game.services.media.DbImageSpriteMap;
import com.btxtech.game.services.media.PreloadedImageSpriteMap;
import com.btxtech.game.wicket.uiservices.ClipPanel;
import com.btxtech.game.wicket.uiservices.CrudRootTableHelper;
import com.btxtech.game.wicket.uiservices.ImageSpriteMapPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Arrays;

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
                dbimageSpriteMap.add(new Label("spriteMapSize", new AbstractReadOnlyModel<Double>() {
                    @Override
                    public Double getObject() {
                        try {
                            return clipService.getImageSpriteMap(dbimageSpriteMap.getModelObject().getId()).getData().length / 1000.0;
                        } catch (Exception e) {
                            return 0.0;
                        }
                    }
                }));
                dbimageSpriteMap.add(new ExternalLink("spriteMapLink", ImageHandler.getImageSpriteMapUrl(dbimageSpriteMap.getModelObject().getId())));
            }

            @Override
            protected CrudRootServiceHelper<DbImageSpriteMap> getCrudRootServiceHelperImpl() {
                return clipService.getImageSpriteMapCrud();
            }

            @Override
            protected void onEditSubmit(DbImageSpriteMap dbImageSpriteMap) {
                PageParameters pageParameters = new PageParameters();
                pageParameters.add(ImageSpriteMapAccessAsync.IMAGE_SPRITE_MAP_ID, Integer.toString(dbImageSpriteMap.getId()));
                setResponsePage(ImageSpriteMapEditor.class, pageParameters);
            }
        };

        form.add(new Button("activate") {
            @Override
            public void onSubmit() {
                clipService.activateImageSpriteMapCache();
            }
        });


        Form preloadedForm = new Form("preloadedForm");
        add(preloadedForm);
        new CrudRootTableHelper<PreloadedImageSpriteMap>("preloadedImageSpriteMaps", "savePreloadedImageSpriteMaps", "createPreloadedImageSpriteMap", false, preloadedForm, false) {

            @Override
            protected void extendedPopulateItem(final Item<PreloadedImageSpriteMap> dbCommonClipItem) {
                displayId(dbCommonClipItem);
                dbCommonClipItem.add(new DropDownChoice<>("type", Arrays.asList(PreloadedImageSpriteMapInfo.Type.values())));
                dbCommonClipItem.add(new ImageSpriteMapPanel("dbImageSpriteMap"));
            }

            @Override
            protected CrudRootServiceHelper<PreloadedImageSpriteMap> getCrudRootServiceHelperImpl() {
                return clipService.getPreloadedSpriteMapCrud();
            }

        };


    }
}