package com.btxtech.game.services.cms;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * User: beat
 * Date: 09.06.2011
 * Time: 12:06:45
 */
@Entity
@DiscriminatorValue("STATIC_PROPERTY")
public class DbStaticProperty extends DbContent {
    @Column(length = 500000)
    private String html;
    private boolean escapeMarkup = true;

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public void setEscapeMarkup(boolean escapeMarkup) {
        this.escapeMarkup = escapeMarkup;
    }

    public boolean getEscapeMarkup() {
        return escapeMarkup;
    }
}
