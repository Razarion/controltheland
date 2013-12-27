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
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.wicket.pages.RazarionPage;
import com.btxtech.game.wicket.uiservices.DisplayPageViewLink;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class CmsPage extends RazarionPage implements IHeaderContributor {
    public static final String DETAIL_CONTENT_ID = "detailId";
    public static final String CREATE_CONTENT_ID = "createId";
    public static final String INVOKE_ID = "invokeId";
    public static final String MESSAGE_ID = "messageId";
    public static final String MESSAGE_ADDITIONAL_PARAMETER = "messageAdditional";
    public static final String RESPONSE_PAGE_ADDITIONAL_PARAMETER = "responsePageAdditional";
    public static final String PAGING_NUMBER = "paging";
    public static final String SORT_INFO = "sort";
    public static final char SORT_ASCENDING = 'a';
    public static final char SORT_DESCENDING = 'd';
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
            add(new Label("title", dbPage.getDbI18nName().getString(getLocale())));
            add(new Menu("menu", dbPage.getMenu(), contentContext));
            add(new Header("header", dbPage));
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

    @Override
    protected void onBeforeRender() {
        DbPage dbPage = (DbPage) getDefaultModelObject();
        try {
            userTrackingService.pageAccess(dbPage.getName(), contentContext.getPageParameters().toString());
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
        super.onBeforeRender();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        cmsUiService.renderCmsCssHead(response, pageId);
    }

}
