package com.btxtech.game.services.cms;

import java.util.Collection;
import java.util.Collections;

/**
 * User: beat
 * Date: 09.06.2011
 * Time: 11:55:54
 */
public class DbPropertyRow extends DbContent {
    private String name;
    private DbProperty dbProperty;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DbProperty getDbProperty() {
        return dbProperty;
    }

    public void setDbProperty(DbProperty dbProperty) {
        this.dbProperty = dbProperty;
    }

    @Override
    public Collection<? extends DbContent> getChildren() {
        return Collections.singletonList(dbProperty);
    }
}
