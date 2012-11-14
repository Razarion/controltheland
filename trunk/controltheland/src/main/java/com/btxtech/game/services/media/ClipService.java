package com.btxtech.game.services.media;

import com.btxtech.game.jsre.client.common.info.ClipInfo;
import com.btxtech.game.jsre.client.common.info.PreloadedImageSpriteMapInfo;
import com.btxtech.game.jsre.client.common.info.ImageSpriteMapInfo;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.common.ImageHolder;

import java.util.Collection;

/**
 * User: beat
 * Date: 07.10.12
 * Time: 18:42
 */
public interface ClipService {
    CrudRootServiceHelper<DbClip> getClipLibraryCrud();

    CrudRootServiceHelper<PreloadedImageSpriteMap> getPreloadedSpriteMapCrud();

    CrudRootServiceHelper<DbImageSpriteMap> getImageSpriteMapCrud();

    Collection<ImageSpriteMapInfo> getImageSpriteMapLibrary();

    Collection<ClipInfo> getClipLibrary();

    PreloadedImageSpriteMapInfo getPreloadedImageSpriteMapInfo();

    ImageHolder getImageSpriteMap(int imageSpriteMapId);

    void saveImageSpriteMap(ImageSpriteMapInfo imageSpriteMapInfo, String[] overriddenImages);

    void activateImageSpriteMapCache();
}
    