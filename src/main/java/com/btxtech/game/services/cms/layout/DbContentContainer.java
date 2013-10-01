package com.btxtech.game.services.cms.layout;


import com.btxtech.game.services.common.CrudListChildServiceHelper;
import com.btxtech.game.services.common.CrudParent;
import com.btxtech.game.services.user.UserService;
import org.hibernate.annotations.Cascade;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 14.06.2011
 * Time: 16:32:25
 */
@Entity
@DiscriminatorValue("CONTENT_CONTAINER")
public class DbContentContainer extends DbContent implements DataProviderInfo, CrudParent {
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "parent_id")
    @org.hibernate.annotations.IndexColumn(name = "orderIndex", base = 0)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private List<DbContent> dbContents;
    private String springBeanName;
    private String contentProviderGetter;
    private String expression;
    @Transient
    private CrudListChildServiceHelper<DbContent> contentCrud;

    @Override
    public String getSpringBeanName() {
        return springBeanName;
    }

    public void setSpringBeanName(String springBeanName) {
        this.springBeanName = springBeanName;
    }

    @Override
    public String getContentProviderGetter() {
        return contentProviderGetter;
    }

    public void setContentProviderGetter(String contentProviderGetter) {
        this.contentProviderGetter = contentProviderGetter;
    }

    @Override
    public String getExpression() {
        return expression;
    }

    @Override
    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Override
    public Collection<DbContent> getChildren() {
        return dbContents;
    }

    @Override
    public void init(UserService userService) {
        dbContents = new ArrayList<DbContent>();
    }

    public CrudListChildServiceHelper<DbContent> getContentCrud() {
        if (contentCrud == null) {
            contentCrud = new CrudListChildServiceHelper<DbContent>(dbContents, DbContent.class, this);
        }
        return contentCrud;
    }
}
