package com.btxtech.game.services;

import com.btxtech.game.services.terrain.DbTerrainSetting;
import com.btxtech.game.services.terrain.TerrainService;
import org.hibernate.SessionFactory;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * User: beat
 * Date: 21.02.2011
 * Time: 20:41:45
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:BaseTestService-context.xml")
public class BaseTestService {
    private HibernateTemplate hibernateTemplate;
    @Autowired
    private TerrainService terrainService;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    public HibernateTemplate getHibernateTemplate() {
        return hibernateTemplate;
    }

    public void setupMinimalTerrain() {
        terrainService.getDbTerrainSettingCrudServiceHelper().createDbChild();
        DbTerrainSetting dbTerrainSetting = terrainService.getDbTerrainSettingCrudServiceHelper().readDbChildren().iterator().next();
        dbTerrainSetting.setRealGame(true);
        dbTerrainSetting.setTileXCount(100);
        dbTerrainSetting.setTileYCount(100);
        dbTerrainSetting.setTileWidth(100);
        dbTerrainSetting.setTileHeight(100);
        terrainService.getDbTerrainSettingCrudServiceHelper().updateDbChild(dbTerrainSetting);
        terrainService.getDbTerrainImageCrudServiceHelper().createDbChild();
        terrainService.getDbSurfaceImageCrudServiceHelper().createDbChild();

        SessionFactoryUtils.initDeferredClose(hibernateTemplate.getSessionFactory());
        terrainService.activateTerrain();
        SessionFactoryUtils.processDeferredClose(hibernateTemplate.getSessionFactory());
    }
}
