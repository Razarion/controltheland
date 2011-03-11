package com.btxtech.game.services;

import com.btxtech.game.services.terrain.DbTerrainSetting;
import com.btxtech.game.services.terrain.TerrainService;
import com.btxtech.game.services.utg.UserGuidanceService;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletRequest;

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
    private UserGuidanceService userGuidanceService;
    @Autowired
    private TerrainService terrainService;
    @Autowired
    private ConfigurableListableBeanFactory configurableListableBeanFactory;
    private SessionHolder sessionHolder;
    private MockHttpServletRequest mockHttpServletRequest;
    private MockHttpSession mockHttpSession;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Before
    public void setup() {
        configurableListableBeanFactory.registerResolvableDependency(ServletRequest.class, new ObjectFactory<ServletRequest>() {
            // This is used to inject HttpServletRequest into SessionImpl
            @Override
            public ServletRequest getObject() throws BeansException {
                return mockHttpServletRequest;
            }
        });
    }

    protected HibernateTemplate getHibernateTemplate() {
        return hibernateTemplate;
    }

    protected void setupMinimalTerrain() {
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

    protected void beginOpenSessionInViewFilter() {
        if (sessionHolder != null) {
            throw new IllegalStateException("SessionHolder is NOT null. afterOpenSessionInViewFilter() was not called.");
        }

        Session session = SessionFactoryUtils.getSession(getHibernateTemplate().getSessionFactory(), true);
        session.setFlushMode(FlushMode.AUTO);
        sessionHolder = new SessionHolder(session);
        TransactionSynchronizationManager.bindResource(getHibernateTemplate().getSessionFactory(), sessionHolder);
    }

    protected void endOpenSessionInViewFilter() {
        if (sessionHolder == null) {
            throw new IllegalStateException("SessionHolder is null. Call beforeOpenSessionInViewFilter() first.");
        }
        SessionFactoryUtils.closeSession(sessionHolder.getSession());
        TransactionSynchronizationManager.unbindResource(getHibernateTemplate().getSessionFactory());
        sessionHolder = null;
    }

    protected void beginHttpSession() {
        if (mockHttpSession != null) {
            throw new IllegalStateException("mockHttpSession is not null");
        }
        mockHttpSession = new MockHttpSession();
    }

    protected void endHttpSession() {
        if (mockHttpSession == null) {
            throw new IllegalStateException("mockHttpSession is null");
        }
        mockHttpSession.invalidate();
        mockHttpSession = null;
    }

    protected void beginHttpRequest() {
        if (mockHttpServletRequest != null) {
            throw new IllegalStateException("mockHttpServletRequest is not null");
        }
        if (mockHttpSession == null) {
            throw new IllegalStateException("mockHttpSession is null");
        }
        mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setSession(mockHttpSession);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockHttpServletRequest));
    }

    protected void endHttpRequest() {
        if (mockHttpServletRequest == null) {
            throw new IllegalStateException("mockHttpServletRequest not null");
        }
        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).requestCompleted();
        RequestContextHolder.resetRequestAttributes();
        mockHttpServletRequest = null;
    }

    protected void beginHttpRequestAndOpenSessionInViewFilter() {
        beginHttpRequest();
        beginOpenSessionInViewFilter();
    }


    protected void endHttpRequestAndOpenSessionInViewFilter() {
        endOpenSessionInViewFilter();
        endHttpRequest();
    }

}
