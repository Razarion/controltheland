package com.btxtech.game.wicket.uiservices;

import com.btxtech.game.services.cms.CmsService;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.markup.html.WebResource;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.resource.ByteArrayResource;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.value.ValueMap;

/**
 * User: beat
 * Date: 10.03.2011
 * Time: 10:49:56
 */
public class CmsImageResource extends WebResource {
    public static final String CMS_SHARED_IMAGE_RESOURCES = "cms";
    private static final String ID = "id";

    public enum ImageId {
        START,
        INFO
    }

    @SpringBean
    private CmsService cmsService;

    public static Image createImage(String id, ImageId imageId) {
        return new Image(id, new ResourceReference(CmsImageResource.CMS_SHARED_IMAGE_RESOURCES), new ValueMap(ID + "=" + imageId.ordinal()));
    }

    public CmsImageResource() {
        // Inject CmsService
        InjectorHolder.getInjector().inject(this);
    }

    @Override
    public IResourceStream getResourceStream() {
        int id = Integer.parseInt(getParameters().getString(ID));
        ImageId imageId = ImageId.values()[id];

        switch (imageId) {
            case START:
                return new ByteArrayResource(cmsService.getHomeContentStyleDTO().getDbCmsHomeLayout().getStartImageContentType(),
                        cmsService.getHomeContentStyleDTO().getDbCmsHomeLayout().getStartImage()).getResourceStream();
            case INFO:
                return new ByteArrayResource(cmsService.getHomeContentStyleDTO().getDbCmsHomeLayout().getInfoImageContentType(),
                        cmsService.getHomeContentStyleDTO().getDbCmsHomeLayout().getInfoImage()).getResourceStream();
            default:
                throw new IllegalArgumentException("Unknown ImageId: " + imageId);
        }

    }
}
