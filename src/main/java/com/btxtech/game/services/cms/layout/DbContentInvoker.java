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
 * Date: 26.07.2011
 * Time: 00:57:49
 */
@Entity
@DiscriminatorValue("INVOKER")
public class DbContentInvoker extends DbContent implements DataProviderInfo, CrudParent {
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    @OrderColumn(name = "orderIndex")
    private List<DbExpressionProperty> dbValueFields;
    private String methodName;
    private String invokeButtonName;
    private String cancelButtonName;
    private String springBeanName;
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

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getInvokeButtonName() {
        return invokeButtonName;
    }

    public void setInvokeButtonName(String invokeButtonName) {
        this.invokeButtonName = invokeButtonName;
    }

    public String getCancelButtonName() {
        return cancelButtonName;
    }

    public void setCancelButtonName(String cancelButtonName) {
        this.cancelButtonName = cancelButtonName;
    }

    @Override
    public void setSpringBeanName(String springBeanName) {
        this.springBeanName = springBeanName;
    }

    @Override
    public String getSpringBeanName() {
        return springBeanName;
    }
}
