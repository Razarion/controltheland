package com.btxtech.game.services.cms;

import java.util.Collection;

/**
 * User: beat
 * Date: 06.06.2011
 * Time: 15:26:35
 */
public abstract class DbContent {
    static int TMP_ID = 0; // TODO REMOVE!!!
    private Integer id;

    @Deprecated
    protected DbContent() {
        id = TMP_ID++;// TODO REMOVE!!!
    }

    public Integer getId() {
        return id;
    }

    public abstract Collection<? extends DbContent> getChildren();
}
