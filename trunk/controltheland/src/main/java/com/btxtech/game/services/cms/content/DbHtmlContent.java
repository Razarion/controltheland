package com.btxtech.game.services.cms.content;

import com.btxtech.game.services.cms.layout.DbContentDynamicHtml;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * User: beat
 * Date: 04.07.2011
 * Time: 13:59:08
 */
@Entity(name = "CONTENT_HTML")
public class DbHtmlContent {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(length = 50000)
    private String html;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbContentDynamicHtml dbContentDynamicHtml;

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public DbContentDynamicHtml getDbContentDynamicHtml() {
        return dbContentDynamicHtml;
    }

    public void setDbContentDynamicHtml(DbContentDynamicHtml dbContentDynamicHtml) {
        this.dbContentDynamicHtml = dbContentDynamicHtml;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbHtmlContent)) return false;

        DbHtmlContent that = (DbHtmlContent) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}
