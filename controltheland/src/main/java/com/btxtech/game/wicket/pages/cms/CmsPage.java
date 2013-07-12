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
import com.btxtech.game.services.common.Utils;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.wicket.WebCommon;
import com.btxtech.game.wicket.WicketAuthenticatedWebSession;
import com.btxtech.game.wicket.uiservices.DisplayPageViewLink;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import com.btxtech.game.wicket.uiservices.cms.impl.CmsUiServiceImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptContentHeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.StringHeaderItem;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.template.JavaScriptTemplate;
import org.apache.wicket.util.template.PackageTextTemplate;

import java.util.HashMap;
import java.util.Map;

public class CmsPage extends WebPage implements IHeaderContributor {
    public static final String DETAIL_CONTENT_ID = "detailId";
    public static final String CREATE_CONTENT_ID = "createId";
    public static final String INVOKE_ID = "invokeId";
    public static final String MESSAGE_ID = "messageId";
    public static final String MESSAGE_ADDITIONAL_PARAMETER = "messageAdditional";
    public static final String RESPONSE_PAGE_ADDITIONAL_PARAMETER = "responsePageAdditional";
    public static final String PAGING_NUMBER = "paging";
    public static final String SORT_INFO = "sort";
    public static final String HTML5_KEY = "html5";
    public static final String HTML5_KEY_N = "n";
    public static final String HTML5_KEY_Y = "y";
    public static final char SORT_ASCENDING = 'a';
    public static final char SORT_DESCENDING = 'd';
    public static final String CMS_SHARED_CSS_RESOURCES = "cssResource";
    public static final String CMS_CSS_ID = "id";
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
                    "errorMessage = encodeURI(\"JSDetection Ecxeption:\" + e);" +
                    "pathname = encodeURI(window.location.pathname);" +
                    "var img = document.createElement('img');" +
                    "img.src = '/spring/lsc?e=' + errorMessage + '&t=' + new Date().getTime() + '&p=' + pathname;" +
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
        setDefaultModel(new CompoundPropertyModel<>(new LoadableDetachableModel<DbPage>() {

            @Override
            protected DbPage load() {
                DbPage dbPage;
                if (!pageParameters.get(CmsUtil.ID).isNull()) {
                    if (CmsUtil.NO_HTML5_BROWSER_PAGE_STRING_ID.equals(pageParameters.get(CmsUtil.ID).toString())) {
                        try {
                            dbPage = cmsService.getPredefinedDbPage(CmsUtil.CmsPredefinedPage.NO_HTML5_BROWSER);
                            pageId = dbPage.getId();
                        } catch (CmsPredefinedPageDoesNotExistException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        pageId = pageParameters.get(CmsUtil.ID).toInt();
                        dbPage = cmsService.getPage(pageId);
                    }
                } else if (!pageParameters.get(CmsUtil.SECTION_ID).isNull()) {
                    String section = pageParameters.get(CmsUtil.SECTION_ID).toString();
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
        try {
            contentContext = new ContentContext(pageParameters, getLocale());
            DbPage dbPage = (DbPage) getDefaultModelObject();
            if (dbPage.getPredefinedType() == CmsUtil.CmsPredefinedPage.FACEBOOK_START) {
                cmsUiService.handleFacebookRequest(pageParameters, this);
            }
            add(new Label("title", dbPage.getDbI18nName().getString(getLocale())));
            add(new Menu("menu", dbPage.getMenu(), contentContext));
            add(new Header("header", dbPage));
            // Footer removed due CMS redisgn
            //add(new Footer("footer", dbPage));
            add(new Ads("contentRight", dbPage));
            Form form = new Form("form");
            add(form);
            form.add(cmsUiService.getRootComponent(dbPage, "content", contentContext));
            add(new DisplayPageViewLink("componentTree", this));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        // CSS resource
        PageParameters pageParameters = new PageParameters();
        pageParameters.set(CMS_CSS_ID, pageId);
        response.render(new CssReferenceHeaderItem(new ResourceReference(CMS_SHARED_CSS_RESOURCES) {
            @Override
            public IResource getResource() {
                return new ByteArrayResource("text/css") {
                    @Override
                    protected byte[] getData(Attributes attributes) {
                        int pageId = Utils.parseIntSave(attributes.getParameters().get(CMS_CSS_ID).toString());
                        return cmsService.getPage(pageId).getStyle().getCss().getBytes();
                    }
                };
            }
        }, pageParameters, "screen", null));
        // LSC detection
        PackageTextTemplate jsTemplate = new PackageTextTemplate(CmsUiServiceImpl.class, "LscErrorHandler.js");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("DISPLAY_ERROR_PREFIX", "InPageError(CmsPage)");
        response.render(new StringHeaderItem(new JavaScriptTemplate(jsTemplate).asString(parameters)));
        // Tracking
        if (!userTrackingService.isJavaScriptDetected()) {
            response.render(JavaScriptHeaderItem.forScript(JAVA_SCRIPT_HTML5_DETECTION, null));
        } else {
            response.render(JavaScriptHeaderItem.forScript(JAVA_SCRIPT_HTML5_DETECTION_EMPTY, null));
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
        WicketAuthenticatedWebSession wicketSession = (WicketAuthenticatedWebSession) getSession();

        if (wicketSession.isTrackingCookieIdCookieNeeded()) {
            WebCommon.setTrackingCookie((WebResponse) getResponse(), wicketSession.getTrackingCookieId());
            wicketSession.clearTrackingCookieIdCookieNeeded();
        }
    }
}
