package com.btxtech.game.wicket.pages.cms.content;

import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.services.cms.layout.DbContentBooleanExpressionImage;
import com.btxtech.game.wicket.pages.cms.CmsImageResource;
import com.btxtech.game.wicket.uiservices.BeanIdPathElement;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;

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
        PageParameters pageParameters = new PageParameters();
        pageParameters.set(CmsImageResource.ID, imageId);
        Image image = new Image("image", new PackageResourceReference(CmsUtil.MOUNT_CMS_IMAGES), pageParameters);
        if (dbContentBooleanExpressionImage.getCssClass() != null) {
            image.add(new AttributeModifier("class", dbContentBooleanExpressionImage.getCssClass()));
        }
        add(image);
    }
}
