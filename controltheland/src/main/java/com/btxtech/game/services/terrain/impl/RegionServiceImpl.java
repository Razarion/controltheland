package com.btxtech.game.services.terrain.impl;

import com.btxtech.game.jsre.common.Region;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.terrain.DbRegion;
import com.btxtech.game.services.terrain.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * User: beat
 * Date: 12.09.12
 * Time: 00:15
 */
@Component
public class RegionServiceImpl implements RegionService {
    @Autowired
    private CrudRootServiceHelper<DbRegion> regionCrud;
    //@Autowired
    //private SessionFactory sessionFactory;
    private Map<Integer, Region> regionCache = new HashMap<>();

    @PostConstruct
    public void init() {
        regionCrud.init(DbRegion.class);
    }

    @Override
    public CrudRootServiceHelper<DbRegion> getRegionCrud() {
        return regionCrud;
    }

    @Override
    public Region getRegionFromCache(DbRegion dbRegion) {
        Region region = regionCache.get(dbRegion.getId());
        if (region != null) {
            return region;
        }
        return putDbRegionToCacheInSession(dbRegion);
//        if(HibernateUtil.hasOpenSession(sessionFactory)) {
//
//        } else {
//            HibernateUtil.openSession4InternalCall(sessionFactory);
//            try {
//
//            } finally {
//                HibernateUtil.closeSession4InternalCall(sessionFactory);
//            }
//        }

    }

    private Region putDbRegionToCacheInSession(DbRegion dbRegion) {
        Region region = dbRegion.createRegion();
        regionCache.put(region.getId(), region);
        return region;
    }

    @Override
    @Transactional
    public void saveRegionToDb(Region region) {
        DbRegion dbRegion = regionCrud.readDbChild(region.getId());
        dbRegion.setRegion(region);
        regionCrud.updateDbChild(dbRegion);
        if (regionCache.containsKey(region.getId())) {
            regionCache.put(region.getId(), region);
        }
    }

    @Override
    public Region loadRegionFromDb(int regionId) {
        DbRegion dbRegion = regionCrud.readDbChild(regionId);
        return dbRegion.createRegion();
    }
}
