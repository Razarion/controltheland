package com.btxtech.game.services.cms;

import com.btxtech.game.services.cms.layout.DbContentList;

/**
 * User: beat
 * Date: 25.12.2011
 * Time: 13:49:34
 */
public class CmsSectionInfo {
    private Class clazz;
    private DbContentList dbContentList;
    private String sectionName;
    private int pageId;

    public CmsSectionInfo(Class clazz, DbContentList dbContentList, String sectionName, int pageId) {
        this.clazz = clazz;
        this.dbContentList = dbContentList;
        this.sectionName = sectionName;
        this.pageId = pageId;
    }

    public DbContentList getDbContentList() {
        return dbContentList;
    }

    public boolean isAssignableFrom(Class clazz) {
        return this.clazz.isAssignableFrom(clazz);
    }

    public String getSectionName() {
        return sectionName;
    }

    public int getPageId() {
        return pageId;
    }
}
