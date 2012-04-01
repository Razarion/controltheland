package com.btxtech.game.wicket.pages.cms;

import com.btxtech.game.services.cms.layout.DbContent;
import com.btxtech.game.services.cms.layout.DbContentList;
import com.btxtech.game.services.cms.layout.DbExpressionProperty;
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

    public boolean isSorting(int contentListId) {
        return pageParameters.containsKey(generateSortInfoKey(contentListId));
    }

    public boolean isAscSorting(int contentListId) {
        String sortInfoString = getContentSortInfoString(contentListId);
        if (sortInfoString == null) {
            throw new IllegalStateException("contentListId " + contentListId + " is not sorting");
        }
        return sortInfoString.charAt(0) == CmsPage.SORT_ASCENDING;
    }

    public boolean isSortColumnActive(int contentListId, DbExpressionProperty dbExpressionProperty) {
        String sortInfoString = getContentSortInfoString(contentListId);
        return sortInfoString != null && sortInfoString.substring(1, sortInfoString.length()).equals(dbExpressionProperty.getName());
    }

    public void setDefaultSort(DbContentList dbContentList) {
        for (DbContent dbContent : dbContentList.getColumnsCrud().readDbChildren()) {
            if (dbContent instanceof DbExpressionProperty) {
                DbExpressionProperty dbExpressionProperty = (DbExpressionProperty) dbContent;
                if (dbExpressionProperty.isDefaultSortable()) {
                    if (dbExpressionProperty.isDefaultSortableAsc()) {
                        pageParameters.put(generateSortInfoKey(dbContentList.getId()), CmsPage.SORT_ASCENDING + dbExpressionProperty.getName());
                    } else {
                        pageParameters.put(generateSortInfoKey(dbContentList.getId()), CmsPage.SORT_DESCENDING + dbExpressionProperty.getName());
                    }
                }
            }
        }
    }

    public static String getSortInfo(String columnName, int contentListId, ContentContext contentContext) {
        String oldSortInfo = contentContext.getContentSortInfoString(contentListId);
        String newSortInfo;
        if (oldSortInfo != null && oldSortInfo.length() > 1 && oldSortInfo.substring(1, oldSortInfo.length()).equals(columnName)) {
            if (oldSortInfo.charAt(0) == CmsPage.SORT_DESCENDING) {
                newSortInfo = CmsPage.SORT_ASCENDING + columnName;
            } else {
                newSortInfo = CmsPage.SORT_DESCENDING + columnName;
            }
        } else {
            newSortInfo = CmsPage.SORT_DESCENDING + columnName;
        }
        return newSortInfo;
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
