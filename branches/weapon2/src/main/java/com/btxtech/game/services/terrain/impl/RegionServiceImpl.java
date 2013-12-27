package com.btxtech.game.services.terrain.impl;

import com.btxtech.game.jsre.common.Region;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.terrain.DbRegion;
import com.btxtech.game.services.terrain.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

/**
 * User: beat
 * Date: 12.09.12
 * Time: 00:15
 */
@Component
public class RegionServiceImpl implements RegionService {
    @Autowired
    private CrudRootServiceHelper<DbRegion> regionCrud;

    @PostConstruct
    public void init() {
        regionCrud.init(DbRegion.class);
    }

    @Override
    public CrudRootServiceHelper<DbRegion> getRegionCrud() {
        return regionCrud;
    }

    @Override
    @Transactional
    public void saveRegionToDb(Region region) {
        DbRegion dbRegion = regionCrud.readDbChild(region.getId());
        dbRegion.setRegion(region);
        regionCrud.updateDbChild(dbRegion);
    }

    @Override
    public Region loadRegionFromDb(int regionId) {
        DbRegion dbRegion = regionCrud.readDbChild(regionId);
        return dbRegion.createRegion();
    }
}
