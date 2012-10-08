package com.btxtech.game.services.media.impl;

import com.btxtech.game.jsre.client.common.info.CommonSoundInfo;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.media.DbCommonSound;
import com.btxtech.game.services.media.DbSound;
import com.btxtech.game.services.media.SoundService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * User: beat
 * Date: 13.08.12
 * Time: 23:15
 */
@Component(value = "soundService")
public class SoundServiceImpl implements SoundService {
    @Autowired
    private CrudRootServiceHelper<DbSound> soundLibraryCrud;
    @Autowired
    private CrudRootServiceHelper<DbCommonSound> commonSoundCrud;
    private Log log = LogFactory.getLog(SoundServiceImpl.class);

    @PostConstruct
    public void setup() {
        soundLibraryCrud.init(DbSound.class);
        commonSoundCrud.init(DbCommonSound.class);
    }

    @Override
    public CrudRootServiceHelper<DbSound> getSoundLibraryCrud() {
        return soundLibraryCrud;
    }

    @Override
    public CrudRootServiceHelper<DbCommonSound> getCommonSoundCrud() {
        return commonSoundCrud;
    }

    @Override
    public CommonSoundInfo getCommonSoundInfo() {
        CommonSoundInfo commonSoundInfo = new CommonSoundInfo();
        try {
            for (DbCommonSound dbCommonSound : commonSoundCrud.readDbChildren()) {
                switch (dbCommonSound.getType()) {
                    case UNIT_LOST:
                        commonSoundInfo.setUnitLostSoundId(dbCommonSound.getDbSound().getId());
                        break;
                    case BUILDING_LOST:
                        commonSoundInfo.setBuildingLostSoundId(dbCommonSound.getDbSound().getId());
                        break;
                    case UNIT_KILLED:
                        commonSoundInfo.setUnitKilledSoundId(dbCommonSound.getDbSound().getId());
                        break;
                    case BUILDING_KILLED:
                        commonSoundInfo.setBuildingKilledSoundId(dbCommonSound.getDbSound().getId());
                        break;
                    case BACKGROUND_MUSIC:
                        commonSoundInfo.setBackgroundMusicSoundId(dbCommonSound.getDbSound().getId());
                        break;
                }
            }
        } catch (Exception e) {
            log.error("Setup CommonSoundInfo failed", e);
        }
        return commonSoundInfo;
    }
}
