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
import com.btxtech.game.services.cms.DbPage;
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
    public static final String ID = "page";
    private static final String CHILD_ID = "childId";
    public static final String DETAIL_CONTENT_ID = "detailId";
    public static final String CREATE_CONTENT_ID = "createId";
    public static final String INVOKE_ID = "invokeId";
    public static final String MESSAGE_ID = "messageId";
    public static final String JAVA_SCRIPT_DETECTION = "var f = document.createElement('script');\n" +
            "f.setAttribute(\"type\", \"text/javascript\");\n" +
            "f.setAttribute(\"src\", \"/spring/statJS\");\n" +
            "document.getElementsByTagName(\"head\")[0].appendChild(f)";

    @SpringBean
    private CmsService cmsService;
    @SpringBean
    private CmsUiService cmsUiService;
    @SpringBean
    private UserTrackingService userTrackingService;
    private int pageId;
    public static final int MAX_LEVELS = 20;

    public CmsPage(final PageParameters pageParameters) {
        setDefaultModel(new CompoundPropertyModel<DbPage>(new LoadableDetachableModel<DbPage>() {

            @Override
            protected DbPage load() {
                DbPage dbPage;
                if (pageParameters.containsKey(ID)) {
                    pageId = pageParameters.getInt(ID);
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
        DbPage dbPage = (DbPage) getDefaultModelObject();
        add(new Label("title", dbPage.getName()));
        add(CmsCssResource.createCss("css", dbPage));
        add(new Menu("menu", dbPage.getMenu()));
        add(new Header("header", dbPage));
        add(new Footer("footer", dbPage));
        add(new Ads("contentRight", dbPage));
        Form form = new Form("form");
        add(form);
        form.add(cmsUiService.getRootComponent(dbPage, "content", pageParameters));
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
            iHeaderResponse.renderJavascript(JAVA_SCRIPT_DETECTION, null);
        }
    }

    @Override
    protected void onBeforeRender() {
        DbPage dbPage = (DbPage) getDefaultModelObject();
        userTrackingService.pageAccess(dbPage.getName());
        super.onBeforeRender();
        if (userTrackingService.hasCookieToAdd()) {
            WebCommon.addCookieId(((WebResponse) getRequestCycle().getResponse()).getHttpServletResponse(), userTrackingService.getAndClearCookieToAdd());
        }
    }
}
