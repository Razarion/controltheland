package com.btxtech.game.services.terrain;

import com.btxtech.game.jsre.common.Region;
import com.btxtech.game.services.common.CrudRootServiceHelper;

/**
 * User: beat
 * Date: 11.09.12
 * Time: 23:49
 */
public interface RegionService {
    CrudRootServiceHelper<DbRegion> getRegionCrud();

    Region getRegionFromCache(DbRegion dbRegion);

    void saveRegionToDb(Region region);

    Region loadRegionFromDb(int regionId);
}
