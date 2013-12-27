package com.btxtech.game.services.cms.content;

import com.btxtech.game.jsre.client.dialogs.news.NewsEntryInfo;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.user.UserService;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * User: beat
 * Date: 14.06.2011
 * Time: 16:05:23
 */
@Entity(name = "CONTENT_BLOG")
public class DbBlogEntry implements CrudChild {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    @Column(length = 50000)
    private String html;
    private long timeStamp;

    @Override
    public Serializable getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    @Override
    public void init(UserService userService) {
        timeStamp = System.currentTimeMillis();
    }

    @Override
    public void setParent(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getParent() {
        return null;
    }

    public NewsEntryInfo createNewsEntryInfo(int totalCount) {
        return new NewsEntryInfo(name, new Date(timeStamp), html, totalCount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbBlogEntry)) return false;

        DbBlogEntry that = (DbBlogEntry) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}
