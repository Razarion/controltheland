package com.btxtech.game.jsre.client.common.info;

import java.io.Serializable;

/**
 * User: beat
 * Date: 15.08.12
 * Time: 14:45
 */
public class CommonSoundInfo implements Serializable {
    private Integer unitLostSoundId;
    private Integer buildingLostSoundId;
    private Integer unitKilledSoundId;
    private Integer buildingKilledSoundId;
    private Integer backgroundMusicSoundId;

    public Integer getUnitLostSoundId() {
        return unitLostSoundId;
    }

    public void setUnitLostSoundId(Integer unitLostSoundId) {
        this.unitLostSoundId = unitLostSoundId;
    }

    public Integer getBuildingLostSoundId() {
        return buildingLostSoundId;
    }

    public void setBuildingLostSoundId(Integer buildingLostSoundId) {
        this.buildingLostSoundId = buildingLostSoundId;
    }

    public Integer getUnitKilledSoundId() {
        return unitKilledSoundId;
    }

    public void setUnitKilledSoundId(Integer unitKilledSoundId) {
        this.unitKilledSoundId = unitKilledSoundId;
    }

    public Integer getBuildingKilledSoundId() {
        return buildingKilledSoundId;
    }

    public void setBuildingKilledSoundId(Integer buildingKilledSoundId) {
        this.buildingKilledSoundId = buildingKilledSoundId;
    }

    public Integer getBackgroundMusicSoundId() {
        return backgroundMusicSoundId;
    }

    public void setBackgroundMusicSoundId(Integer backgroundMusicSoundId) {
        this.backgroundMusicSoundId = backgroundMusicSoundId;
    }
}
