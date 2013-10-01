package com.btxtech.game.services.cms.layout;

import com.btxtech.game.services.common.CrudListChildServiceHelper;
import com.btxtech.game.services.common.CrudParent;
import com.btxtech.game.services.user.UserService;
import org.hibernate.annotations.Cascade;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 13.07.2011
 * Time: 00:57:49
 */
@Entity
@DiscriminatorValue("CREATE_EDIT")
public class DbContentCreateEdit extends DbContent implements CrudParent {
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    @OrderColumn(name = "orderIndex")
    private List<DbExpressionProperty> dbValueFields;
    @Transient
    private CrudListChildServiceHelper<DbExpressionProperty> valueCrud;

    public CrudListChildServiceHelper<DbExpressionProperty> getValueCrud() {
        if (valueCrud == null) {
            valueCrud = new CrudListChildServiceHelper<DbExpressionProperty>(dbValueFields, DbExpressionProperty.class, this);
        }
        return valueCrud;
    }

    @Override
    public void init(UserService userService) {
        dbValueFields = new ArrayList<DbExpressionProperty>();
    }

    @Override
    public Collection<DbContent> getChildren() {
        return new ArrayList<DbContent>(dbValueFields);
    }
}
