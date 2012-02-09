package com.btxtech.game.wicket.pages.cms.content;

import com.btxtech.game.services.cms.layout.DbContentBooleanExpressionImage;
import com.btxtech.game.wicket.pages.cms.CmsImageResource;
import com.btxtech.game.wicket.uiservices.BeanIdPathElement;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.value.ValueMap;

/**
 * User: beat
 * Date: 25.07.2011
 * Time: 14:01:19
 */
public class ContentBooleanExpressionImage extends Panel {
    @SpringBean
    private CmsUiService cmsUiService;

    public ContentBooleanExpressionImage(String id, DbContentBooleanExpressionImage dbContentBooleanExpressionImage, BeanIdPathElement beanIdPathElement) {
        super(id);

        boolean b = (Boolean) cmsUiService.getDataProviderBean(beanIdPathElement);
        int imageId;
        if (b) {
            imageId = dbContentBooleanExpressionImage.getTrueImage().getId();
        } else {
            imageId = dbContentBooleanExpressionImage.getFalseImage().getId();
        }
        Image image = new Image("image", new ResourceReference(CmsImageResource.CMS_SHARED_IMAGE_RESOURCES), new ValueMap(CmsImageResource.ID + "=" + imageId));
        if (dbContentBooleanExpressionImage.getCssClass() != null) {
            image.add(new SimpleAttributeModifier("class", dbContentBooleanExpressionImage.getCssClass()));
        }
        add(image);
    }
}
