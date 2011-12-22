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

package com.btxtech.game.wicket.pages.cms;

import com.btxtech.game.jsre.common.CmsPredefinedPageDoesNotExistException;
import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.page.DbPage;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.wicket.WebCommon;
import com.btxtech.game.wicket.uiservices.DisplayPageViewLink;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class CmsPage extends WebPage implements IHeaderContributor {
    private static final String CHILD_ID = "childId";
    public static final String DETAIL_CONTENT_ID = "detailId";
    public static final String CREATE_CONTENT_ID = "createId";
    public static final String INVOKE_ID = "invokeId";
    public static final String MESSAGE_ID = "messageId";
    public static final String PAGING_NUMBER = "paging";
    public static final String SORT_INFO = "sort";
    public static final String HTML5_KEY = "html5";
    public static final String HTML5_KEY_N = "n";
    public static final String HTML5_KEY_Y = "y";
    public static final char SORT_ASCENDING = 'a';
    public static final char SORT_DESCENDING = 'd';
    public static final String JAVA_SCRIPT_HTML5_DETECTION = "var value='/spring/statJS?" + HTML5_KEY + "=';" +
            "if(window.HTMLCanvasElement){value+='" + HTML5_KEY_Y + "';}else{value+='" + HTML5_KEY_N + "';}" +
            "var f = document.createElement('img');" +
            "f.setAttribute('src',value);" +
            "f.style.position='absolute';" +
            "f.style.top='0';" +
            "f.style.left='0';" +
            "document.body.appendChild(f);";
    public static final int MAX_LEVELS = 20;

    @SpringBean
    private CmsService cmsService;
    @SpringBean
    private CmsUiService cmsUiService;
    @SpringBean
    private UserTrackingService userTrackingService;
    private int pageId;
    private ContentContext contentContext;

    public CmsPage(final PageParameters pageParameters) {
        setDefaultModel(new CompoundPropertyModel<DbPage>(new LoadableDetachableModel<DbPage>() {

            @Override
            protected DbPage load() {
                DbPage dbPage;
                if (pageParameters.containsKey(CmsUtil.ID)) {
                    if (CmsUtil.NO_HTML5_BROWSER_PAGE_STRING_ID.equals(pageParameters.get(CmsUtil.ID))) {
                        try {
                            dbPage = cmsService.getPredefinedDbPage(CmsUtil.CmsPredefinedPage.NO_HTML5_BROWSER);
                            pageId = dbPage.getId();
                        } catch (CmsPredefinedPageDoesNotExistException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        pageId = pageParameters.getInt(CmsUtil.ID);
                        dbPage = cmsService.getPage(pageId);
                    }
                } else {
                    try {
                        dbPage = cmsService.getPredefinedDbPage(CmsUtil.CmsPredefinedPage.HOME);
                        pageId = dbPage.getId();
                    } catch (CmsPredefinedPageDoesNotExistException e) {
                        throw new RuntimeException(e);
                    }
                }
                return dbPage;
            }
        }));
        contentContext = new ContentContext(pageParameters);
        DbPage dbPage = (DbPage) getDefaultModelObject();
        add(new Label("title", dbPage.getName()));
        add(CmsCssResource.createCss("css", dbPage));
        add(new Menu("menu", dbPage.getMenu(), contentContext));
        add(new Header("header", dbPage));
        add(new Footer("footer", dbPage));
        add(new Ads("contentRight", dbPage));
        Form form = new Form("form");
        add(form);
        form.add(cmsUiService.getRootComponent(dbPage, "content", contentContext));
        add(new DisplayPageViewLink("componentTree", this));
    }

    @Override
    public boolean isVisible() {
        DbPage dbPage = (DbPage) getDefaultModelObject();
        return cmsUiService.isPageAccessAllowed(dbPage);
    }

    public static String getChildUrlParameter(int level) {
        if (level == 0) {
            return CHILD_ID;
        } else {
            return CHILD_ID + level;
        }
    }

    @Override
    public void renderHead(IHeaderResponse iHeaderResponse) {
        if (!userTrackingService.isJavaScriptDetected()) {
            iHeaderResponse.renderOnLoadJavascript(JAVA_SCRIPT_HTML5_DETECTION);
        }
    }

    @Override
    protected void onBeforeRender() {
        DbPage dbPage = (DbPage) getDefaultModelObject();
        userTrackingService.pageAccess(dbPage.getName(), contentContext.getPageParameters().toString());
        super.onBeforeRender();
        if (userTrackingService.hasCookieToAdd()) {
            WebCommon.addCookieId(((WebResponse) getRequestCycle().getResponse()).getHttpServletResponse(), userTrackingService.getAndClearCookieToAdd());
        }
    }
}
