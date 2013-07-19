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

package com.btxtech.game.wicket;

import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.services.cms.InvalidUrlException;
import com.btxtech.game.services.cms.NoDbContentInCacheException;
import com.btxtech.game.services.cms.NoDbPageException;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.common.NoSuchChildException;
import com.btxtech.game.services.common.Utils;
import com.btxtech.game.services.mgmt.MgmtService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.wicket.pages.FacebookAppStart;
import com.btxtech.game.wicket.pages.FacebookAutoLogin;
import com.btxtech.game.wicket.pages.Game;
import com.btxtech.game.wicket.pages.cms.CmsImageResource;
import com.btxtech.game.wicket.pages.cms.CmsItemTypeImageResource;
import com.btxtech.game.wicket.pages.cms.CmsPage;
import com.btxtech.game.wicket.pages.mgmt.MgmtPage;
import com.btxtech.game.wicket.uiservices.InventoryImageResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.authorization.IUnauthorizedComponentInstantiationListener;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.core.request.mapper.MountedMapper;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.PageExpiredException;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.UrlPathPageParametersEncoder;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * User: beat Date: May 31, 2009 Time: 9:51:34 PM
 */
@Component
public class WicketApplication extends AuthenticatedWebApplication implements ApplicationContextAware {
    @Autowired
    private UserService userService;
    @Autowired
    private MgmtService mgmtService;
    private RuntimeConfigurationType configurationType;
    private Log log = LogFactory.getLog(WicketApplication.class);
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    protected void init() {
        super.init();
        getMarkupSettings().setDefaultMarkupEncoding("UTF-8");
        getRequestCycleSettings().setResponseRequestEncoding("UTF-8");
        getResourceSettings().setThrowExceptionOnMissingResource(false);
        getComponentInstantiationListeners().add(new SpringComponentInjector(this, applicationContext, true));
        getApplicationSettings().setAccessDeniedPage(CmsPage.class);
        mountResource(CmsUtil.MOUNT_CMS_IMAGES, new ResourceReference(Application.class, CmsImageResource.CMS_SHARED_IMAGE_RESOURCES) {
            @Override
            public IResource getResource() {
                return new CmsImageResource();
            }
        });
        mountResource(CmsUtil.MOUNT_SINGLE_ITEM_TYPE_IMAGES, new ResourceReference(Application.class, CmsItemTypeImageResource.CMS_SHARED_IMAGE_RESOURCES) {
            @Override
            public IResource getResource() {
                return new CmsItemTypeImageResource();
            }
        });
        mountResource(CmsUtil.MOUNT_INVENTORY_IMAGES, new ResourceReference(Application.class, CmsUtil.MOUNT_INVENTORY_IMAGES) {
            @Override
            public IResource getResource() {
                return new InventoryImageResource();
            }
        });
        mountPage(CmsUtil.MOUNT_GAME_CMS, CmsPage.class);
        mount(new MountedMapper("/" + CmsUtil.MOUNT_GAME_CMS, CmsPage.class, new UrlPathPageParametersEncoder()));
        mountPage(CmsUtil.MOUNT_GAME, Game.class);
        mountPage(CmsUtil.MOUNT_GAME_FACEBOOK_APP, FacebookAppStart.class);
        mountPage(CmsUtil.MOUNT_GAME_FACEBOOK_AUTO_LOGIN, FacebookAutoLogin.class);
        mountPage(CmsUtil.MOUNT_MANAGEMENT, MgmtPage.class);
        if (!Utils.isTestModeStatic()) {
            getRequestCycleListeners().add(new MyRequestCycleListener());
        }
        getSecuritySettings().setUnauthorizedComponentInstantiationListener(new IUnauthorizedComponentInstantiationListener() {
            @Override
            public void onUnauthorizedInstantiation(org.apache.wicket.Component component) {
                ExceptionHandler.handleException("Unauthorized: " + component);
                WicketApplication.this.onUnauthorizedInstantiation(component);
            }
        });

    }

    @Override
    protected Class<? extends AuthenticatedWebSession> getWebSessionClass() {
        return WicketAuthenticatedWebSession.class;
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return CmsPage.class;
    }

    @Override
    public Class<CmsPage> getHomePage() {
        return CmsPage.class;
    }

    @Override
    public RuntimeConfigurationType getConfigurationType() {
        if (configurationType == null) {
            if (Utils.isTestModeStatic()) {
                configurationType = RuntimeConfigurationType.DEVELOPMENT;
            } else {
                configurationType = RuntimeConfigurationType.DEPLOYMENT;
            }
        }
        return configurationType;
    }

    public final class MyRequestCycleListener extends AbstractRequestCycleListener {
        @Override
        public IRequestHandler onException(RequestCycle cycle, Exception e) {
            if (e instanceof PageExpiredException) {
                log.error("------------------PageExpiredException---------------------------------");
                ExceptionHandler.logParameters(log, userService);
                log.error("URL: " + cycle.getRequest().getOriginalUrl());
                // TODO log.error("Page: " + cycle.getRequest().get());
                log.error(e.getMessage());
                // TODO return cmsUiService.getPredefinedNotFound();
            } else if (CommonJava.getMostInnerThrowable(e) instanceof NoSuchChildException) {
                saveServerDebug(null, e, cycle);
                // TODO return cmsUiService.getPredefinedNotFound();
            } else if (CommonJava.getMostInnerThrowable(e) instanceof InvalidUrlException) {
                saveServerDebug(null, e, cycle);
                // TODO return cmsUiService.getPredefinedNotFound();
            } else if (CommonJava.getMostInnerThrowable(e) instanceof NoDbContentInCacheException) {
                saveServerDebug(null, e, cycle);
                // TODO  return cmsUiService.getPredefinedNotFound();
            } else if (CommonJava.getMostInnerThrowable(e) instanceof NoDbPageException) {
                saveServerDebug(null, e, cycle);
                // TODO return cmsUiService.getPredefinedNotFound();
            } else if (CommonJava.getMostInnerThrowable(e) instanceof NumberFormatException) {
                saveServerDebug(null, e, cycle);
                // TODO return cmsUiService.getPredefinedNotFound();
            } else {
                log.error("------------------CMS Unknown Exception---------------------------------");
                ExceptionHandler.logParameters(log, userService);
                log.error("URL: " + cycle.getRequest().getOriginalUrl());
                // TODO log.error("Page: " + cause);
                log.error("", e);
                // TODO return new CmsPage(new PageParameters());
            }
            return null;
        }

        private void saveServerDebug(final Page cause, Exception e, RequestCycle cycle) {
            mgmtService.saveServerDebug(MgmtService.SERVER_DEBUG_CMS, ((ServletWebRequest) cycle.getRequest()).getContainerRequest(), cause, e);
        }
    }
}
