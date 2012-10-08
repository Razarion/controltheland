package com.btxtech.game.services.media;

import com.btxtech.game.jsre.client.common.info.CommonSoundInfo;
import com.btxtech.game.services.common.CrudRootServiceHelper;

/**
 * User: beat
 * Date: 13.08.12
 * Time: 23:10
 */
public interface SoundService {
    CrudRootServiceHelper<DbSound> getSoundLibraryCrud();

    CrudRootServiceHelper<DbCommonSound> getCommonSoundCrud();

    CommonSoundInfo getCommonSoundInfo();
}
