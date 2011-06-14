package com.btxtech.game.services.cms;

import java.util.Collection;

/**
 * User: beat
 * Date: 09.06.2011
 * Time: 12:06:45
 */
public class DbPropertyBookLink extends DbProperty {
    private String label;
    private DbPage page;

    public String getLabel() {
        return label;
    }

    public DbPage getPage() {
        return page;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setPage(DbPage page) {
        this.page = page;
    }

    @Override
    public Collection<? extends DbContent> getChildren() {
        return null;
    }
}
