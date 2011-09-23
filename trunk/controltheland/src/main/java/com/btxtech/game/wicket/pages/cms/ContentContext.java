package com.btxtech.game.wicket.pages.cms;

import org.apache.wicket.PageParameters;

import java.io.Serializable;

/**
 * User: beat
 * Date: 21.09.2011
 * Time: 11:45:29
 */
public class ContentContext implements Serializable {
    private PageParameters pageParameters;

    public ContentContext(PageParameters pageParameters) {
        this.pageParameters = pageParameters;
    }

    public PageParameters getPageParameters() {
        return pageParameters;
    }

    public String getContentSortInfoString(int contentListId) {
        return pageParameters.getString(generateSortInfoKey(contentListId));
    }

    public static String generateSortInfoKey(int contentListId) {
        return CmsPage.SORT_INFO + Integer.toString(contentListId);
    }

    public boolean hasContentPagingNumber(int contentListId) {
        return pageParameters.containsKey(generatePagingNumberKey(contentListId));
    }

    public int getContentPagingNumber(int contentListId) {
        return pageParameters.getInt(generatePagingNumberKey(contentListId));
    }

    public static String generatePagingNumberKey(int contentListId) {
        return CmsPage.PAGING_NUMBER + Integer.toString(contentListId);
    }
}
