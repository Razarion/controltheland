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
import com.btxtech.game.services.cms.CmsSectionInfo;
import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.page.DbPage;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.wicket.WebCommon;
import com.btxtech.game.wicket.uiservices.DisplayPageViewLink;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    private static final String JAVA_SCRIPT_HTML5_DETECTION_FUNCTION = "jsDetect()";
    private static final String JAVA_SCRIPT_HTML5_DETECTION =
            "function " + JAVA_SCRIPT_HTML5_DETECTION_FUNCTION + "{" +
                    "try {" +
                    "var value='/spring/statJS?" + HTML5_KEY + "=';" +
                    "if(window.HTMLCanvasElement){value+='" + HTML5_KEY_Y + "';}else{value+='" + HTML5_KEY_N + "';}" +
                    "var f = document.createElement('img');" +
                    "f.setAttribute('src',value);" +
                    "f.style.position='absolute';" +
                    "f.style.top='0';" +
                    "f.style.left='0';" +
                    "document.body.appendChild(f);" +
                    "} catch(e) {" +
                    "errorMessage = \"JSDetection Ecxeption:\" + e;" +
                    "var img = document.createElement('img');" +
                    "img.src = '/spring/lsc?e=' + errorMessage + '&t=' + new Date().getTime();" +
                    "document.body.appendChild(img);" +
                    "}" +
                    "}";
    private static final String JAVA_SCRIPT_HTML5_DETECTION_EMPTY =
            "function " + JAVA_SCRIPT_HTML5_DETECTION_FUNCTION + "{}";

    public static final int MAX_LEVELS = 20;

    @SpringBean
    private CmsService cmsService;
    @SpringBean
    private CmsUiService cmsUiService;
    @SpringBean
    private UserTrackingService userTrackingService;
    @SpringBean
    private UserService userService;
    private int pageId;
    private ContentContext contentContext;
    private Log log = LogFactory.getLog(CmsPage.class);

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
                } else if (pageParameters.containsKey(CmsUtil.SECTION_ID)) {
                    String section = pageParameters.getString(CmsUtil.SECTION_ID);
                    CmsSectionInfo cmsSectionInfo = cmsService.getCmsSectionInfo(section);
                    pageId = cmsSectionInfo.getPageId();
                    dbPage = cmsService.getPage(pageId);
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
        if (dbPage.getPredefinedType() == CmsUtil.CmsPredefinedPage.FACEBOOK_START) {
            cmsUiService.handleFacebookRequest(pageParameters, this);
        }
        add(new Label("title", dbPage.getName()));
        add(CmsCssResource.createCss("css", dbPage));
        add(new Menu("menu", dbPage.getMenu(), contentContext));
        add(new Header("header", dbPage));
        // Footer removed due CMS redisgn
        //add(new Footer("footer", dbPage));
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
            return CmsUtil.CHILD_ID;
        } else {
            return CmsUtil.CHILD_ID + level;
        }
    }

    @Override
    public void renderHead(IHeaderResponse iHeaderResponse) {
        if (!userTrackingService.isJavaScriptDetected()) {
            iHeaderResponse.renderJavascript(JAVA_SCRIPT_HTML5_DETECTION, null);
        } else {
            iHeaderResponse.renderJavascript(JAVA_SCRIPT_HTML5_DETECTION_EMPTY, null);
        }
    }

    @Override
    protected void onBeforeRender() {
        DbPage dbPage = (DbPage) getDefaultModelObject();
        try {
            userTrackingService.pageAccess(dbPage.getName(), contentContext.getPageParameters().toString());
        } catch (Exception e) {
            log.error("", e);
        }
        super.onBeforeRender();
        if (userTrackingService.hasCookieToAdd()) {
            WebCommon.addCookieId(((WebResponse) getRequestCycle().getResponse()).getHttpServletResponse(), userTrackingService.getAndClearCookieToAdd());
        }
    }
}
