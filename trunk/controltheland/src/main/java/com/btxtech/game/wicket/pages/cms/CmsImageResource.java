package com.btxtech.game.wicket.pages.cms;

import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.generated.cms.DbCmsImage;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.markup.html.WebResource;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.resource.ByteArrayResource;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.value.ValueMap;

/**
 * User: beat Date: 01.06.2011 Time: 10:49:56
 */
public class CmsImageResource extends WebResource {
	public static final String CMS_SHARED_IMAGE_RESOURCES = "cmsimg";
	public static final String PATH = "/cmsimg";
	private static final String ID = "id";

	@SpringBean
	private CmsService cmsService;

	//May not working due to the mounting to PATH
	//public static Image createImage(String id, int imageId) {
	//	return new Image(id, new ResourceReference(CMS_SHARED_IMAGE_RESOURCES), new ValueMap(ID + "=" + imageId));
	//}

	public CmsImageResource() {
		// Inject CmsService
		InjectorHolder.getInjector().inject(this);
	}

	@Override
	public IResourceStream getResourceStream() {
        int imgId = Integer.parseInt(getParameters().getString(ID));
        DbCmsImage dbCmsImage = cmsService.getDbCmsImage(imgId);
        return new ByteArrayResource(dbCmsImage.getContentType(), dbCmsImage.getData()).getResourceStream();
	}
}
