package com.btxtech.game.wicket.uiservices;

import com.btxtech.game.jsre.mapeditor.TerrainEditorAsync;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.services.terrain.DbRegion;
import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.utg.DbLevel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.io.Serializable;

/**
 * User: beat
 * Date: 13.09.12
 * Time: 11:58
 */
public class TerrainLinkHelper implements Serializable {
    private String pageParamRootType;
    private String pageParamRootId;

    public TerrainLinkHelper(DbPlanet dbPlanet) {
        pageParamRootType = TerrainEditorAsync.ROOT_TYPE_PLANET;
        pageParamRootId = Integer.toString(dbPlanet.getId());
    }

    public TerrainLinkHelper(DbTutorialConfig dbTutorialConfig) {
        pageParamRootType = TerrainEditorAsync.ROOT_TYPE_MISSION;
        pageParamRootId = Integer.toString(dbTutorialConfig.getId());
    }

    public TerrainLinkHelper(DbLevel dbLevel) {
        if (dbLevel.getDbPlanet() != null) {
            pageParamRootType = TerrainEditorAsync.ROOT_TYPE_PLANET;
            pageParamRootId = Integer.toString(dbLevel.getDbPlanet().getId());
        }
    }

    public PageParameters createRegionEditorPageParameters(DbRegion dbRegion) {
        PageParameters pageParameters =createTerrainEditorPageParameters();
        pageParameters.add(TerrainEditorAsync.REGION_ID, Integer.toString(dbRegion.getId()));
        return pageParameters;
    }

    public PageParameters createTerrainEditorPageParameters() {
        PageParameters pageParameters = new PageParameters();
        pageParameters.add(TerrainEditorAsync.ROOT_TYPE, pageParamRootType);
        pageParameters.add(TerrainEditorAsync.ROOT_ID, pageParamRootId);
        return pageParameters;
    }

    public boolean hasAllParameters() {
        return pageParamRootType != null && pageParamRootId != null;
    }
}
