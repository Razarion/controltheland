/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.services.gwt;

import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.jsre.mapeditor.EditorInfo;
import com.btxtech.game.jsre.mapeditor.GameEditor;
import com.btxtech.game.services.terrain.TerrainService;
import java.util.Collection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * User: beat
 * Date: Sep 2, 2009
 * Time: 8:32:46 PM
 */
@Component("terrainEditor")
public class GameEditoImpl implements GameEditor {
    @Autowired
    private TerrainService terrainService;
    private Log log = LogFactory.getLog(GameEditoImpl.class);

    @Override
    public EditorInfo getEditorInfo(){
        try {
            EditorInfo editorInfo = new EditorInfo();
            editorInfo.setTerrainSettings(terrainService.getTerrainSettings());
            editorInfo.setTerrainImagePositions(terrainService.getTerrainImagePositions());
            editorInfo.setTerrainImages(terrainService.getTerrainImages());
            return editorInfo;
        } catch (Throwable t) {
            log.error("", t);
            return null;
        }
    }

    @Override
    public void saveTerrainImagePositions(Collection<TerrainImagePosition> terrainImagePositions) {
        try {
            terrainService.saveAndActivateTerrainImagePositions(terrainImagePositions);
        } catch (Throwable t) {
            log.error("", t);
        }
    }
}