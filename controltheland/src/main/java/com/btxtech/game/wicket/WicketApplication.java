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
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.common.Utils;
import com.btxtech.game.wicket.pages.FacebookAppNickName;
import com.btxtech.game.wicket.pages.FacebookAppStart;
import com.btxtech.game.wicket.pages.FacebookAutoLogin;
import com.btxtech.game.wicket.pages.Game;
import com.btxtech.game.wicket.pages.InvitationStart;
import com.btxtech.game.wicket.pages.cms.CmsImageResource;
import com.btxtech.game.wicket.pages.cms.CmsItemTypeImageResource;
import com.btxtech.game.wicket.pages.cms.CmsPage;
import com.btxtech.game.wicket.pages.mgmt.MgmtPage;
import com.btxtech.game.wicket.uiservices.InventoryImageResource;
import org.apache.wicket.Application;
import org.apache.wicket.DefaultExceptionMapper;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.authorization.IUnauthorizedComponentInstantiationListener;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.core.request.mapper.MountedMapper;
import org.apache.wicket.core.request.mapper.ResourceMapper;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.IExceptionMapper;
import org.apache.wicket.request.mapper.parameter.UrlPathPageParametersEncoder;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.IProvider;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * User: beat Date: May 31, 2009 Time: 9:51:34 PM
 */
@Component
public class WicketApplication extends AuthenticatedWebApplication implements ApplicationContextAware {
    private RuntimeConfigurationType configurationType;
    private IExceptionMapper exceptionMapper;
    private ApplicationContext applicationContext;
    private IProvider<IExceptionMapper> exceptionMapperProvider;

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
        ResourceReference cmsImageResourceReference = new ResourceReference(Application.class, CmsUtil.MOUNT_CMS_IMAGES) {
            @Override
            public IResource getResource() {
                return new CmsImageResource();
            }
        };
        mount(new ResourceMapper("/" + CmsUtil.MOUNT_CMS_IMAGES, cmsImageResourceReference, new UrlPathPageParametersEncoder()));
        ResourceReference cmsSingleItemImageResourceReference = new ResourceReference(Application.class, CmsUtil.MOUNT_SINGLE_ITEM_TYPE_IMAGES) {
            @Override
            public IResource getResource() {
                return new CmsItemTypeImageResource();
            }
        };
        mount(new ResourceMapper("/" + CmsUtil.MOUNT_SINGLE_ITEM_TYPE_IMAGES, cmsSingleItemImageResourceReference, new UrlPathPageParametersEncoder()));
        ResourceReference cmsInventoryImageResourceReference = new ResourceReference(Application.class, CmsUtil.MOUNT_INVENTORY_IMAGES) {
            @Override
            public IResource getResource() {
                return new InventoryImageResource();
            }
        };
        mount(new ResourceMapper("/" + CmsUtil.MOUNT_INVENTORY_IMAGES, cmsInventoryImageResourceReference, new UrlPathPageParametersEncoder()));
        mountPage(CmsUtil.MOUNT_GAME_CMS, CmsPage.class);
        mount(new MountedMapper("/" + CmsUtil.MOUNT_GAME_CMS, CmsPage.class, new UrlPathPageParametersEncoder()));
        mountPage(CmsUtil.MOUNT_GAME, Game.class);
        mount(new MountedMapper("/" + CmsUtil.MOUNT_GAME, Game.class, new UrlPathPageParametersEncoder()));
        mountPage(CmsUtil.MOUNT_GAME_FACEBOOK_APP, FacebookAppStart.class);
        mountPage(CmsUtil.MOUNT_GAME_FACEBOOK_NICK_NAME, FacebookAppNickName.class);
        mountPage(CmsUtil.MOUNT_GAME_FACEBOOK_AUTO_LOGIN, FacebookAutoLogin.class);
        mountPage(CmsUtil.MOUNT_INVITATION_START, InvitationStart.class);
        mountPage(CmsUtil.MOUNT_MANAGEMENT, MgmtPage.class);
        exceptionMapperProvider = new IProvider<IExceptionMapper>() {
            @Override
            public IExceptionMapper get() {
                if (exceptionMapper == null) {
                    if (Utils.isTestModeStatic()) {
                        exceptionMapper = new DefaultExceptionMapper();
                    } else {
                        exceptionMapper = new RazarionExceptionMapper();
                    }
                }
                return exceptionMapper;
            }
        };
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

    @Override
    public IProvider<IExceptionMapper> getExceptionMapperProvider() {
        return exceptionMapperProvider;
    }
}
