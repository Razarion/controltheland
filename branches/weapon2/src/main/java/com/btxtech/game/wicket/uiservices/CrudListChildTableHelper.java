package com.btxtech.game.wicket.uiservices;

import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.CrudListChildServiceHelper;
import com.btxtech.game.services.common.RuServiceHelper;
import org.apache.wicket.markup.html.WebMarkupContainer;

import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 14.03.2011
 * Time: 13:16:36
 */
public abstract class CrudListChildTableHelper<P, T extends CrudChild> extends AbstractCrudRootTableHelper<T> {
    public CrudListChildTableHelper(String tableId, String saveId, String createId, final boolean showEdit, WebMarkupContainer markupContainer, final boolean showOrderButtons) {
        super(tableId, saveId, createId, showEdit, markupContainer, showOrderButtons);
    }

    protected abstract RuServiceHelper<P> getRuServiceHelper();

    protected abstract P getParent();

    protected abstract CrudListChildServiceHelper<T> getCrudListChildServiceHelperImpl();

    @Override
    protected Collection<T> readDbChildren() {
        return getCrudListChildServiceHelperImpl().readDbChildren();
    }

    @Override
    protected void deleteChild(T modelObject) {
        getRuServiceHelper().removeChildAndUpdate(getParent(), getCrudListChildServiceHelperImpl(), modelObject);
    }

    @Override
    protected void updateDbChildren(List<T> children) {
        getRuServiceHelper().updateDbEntity(getParent());
    }

    @Override
    protected T createDbChild() {
        T t = getCrudListChildServiceHelperImpl().createDbChild();
        getRuServiceHelper().updateDbEntity(getParent());
        return t;
    }

    @Override
    protected <C extends T> C createDbChild(Class<C> createClass) {
        C c = (C) getCrudListChildServiceHelperImpl().createDbChild(createClass);
        getRuServiceHelper().updateDbEntity(getParent());
        return c;
    }
}
