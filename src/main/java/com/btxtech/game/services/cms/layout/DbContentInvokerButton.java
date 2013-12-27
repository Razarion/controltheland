package com.btxtech.game.services.cms.layout;

import org.hibernate.annotations.Cascade;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.Collection;

/**
 * User: beat
 * Date: 26.07.2011
 * Time: 01:32:39
 */
@Entity
@DiscriminatorValue("INVOKER_BUTTON")
public class DbContentInvokerButton extends DbContent {
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private DbContentInvoker dbContentInvoker;

    public DbContentInvoker getDbContentInvoker() {
        return dbContentInvoker;
    }

    public void setDbContentInvoker(DbContentInvoker dbContentInvoker) {
        this.dbContentInvoker = dbContentInvoker;
    }

    @Override
    public Collection<DbContent> getChildren() {
        Collection<DbContent> collection = new ArrayList<DbContent>();
        collection.add(dbContentInvoker);
        return collection;
    }
}
