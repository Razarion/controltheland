package com.btxtech.game.services.cms;

import com.btxtech.game.jsre.client.dialogs.news.NewsEntryInfo;
import com.btxtech.game.jsre.common.packets.UserAttentionPacket;
import com.btxtech.game.services.cms.content.DbBlogEntry;
import com.btxtech.game.services.cms.content.DbWikiSection;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.user.User;

/**
 * User: beat
 * Date: 22.06.2011
 * Time: 13:12:04
 */
public interface ContentService {
    CrudRootServiceHelper<DbBlogEntry> getBlogEntryCrudRootServiceHelper();

    CrudRootServiceHelper<DbWikiSection> getWikiSectionCrudRootServiceHelper();

    String getDynamicHtml(int contentId);

    void setDynamicHtml(int contentId, String value);

    NewsEntryInfo getNewsEntry(int index);

    void createNewsEntryAndSendUserAttentionPacket(String title, String content);

    void fillUserAttentionPacket(User user, UserAttentionPacket userAttentionPacket);
}
