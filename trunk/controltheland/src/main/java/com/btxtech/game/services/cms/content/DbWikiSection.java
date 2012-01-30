package com.btxtech.game.services.cms.content;

import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.user.UserService;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * User: beat
 * Date: 03.07.2011
 * Time: 12:35:50
 */
@Entity(name = "CONTENT_WIKI_SECTION")
public class DbWikiSection implements CrudChild {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(length = 50000)
    private String html;
    private String name;

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

    @Override
    public void init(UserService userService) {
    }

    @Override
    public void setParent(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getParent() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbWikiSection)) return false;

        DbWikiSection that = (DbWikiSection) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
