package com.btxtech.game.wicket.uiservices;

import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.RuServiceHelper;
import org.apache.wicket.markup.html.WebMarkupContainer;

import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 14.03.2011
 * Time: 13:16:36
 */
public abstract class CrudChildTableHelper<P, T extends CrudChild> extends AbstractCrudRootTableHelper<T> {
    public CrudChildTableHelper(String tableId, String saveId, String createId, final boolean showEdit, WebMarkupContainer markupContainer, final boolean showOrderButtons) {
        super(tableId, saveId, createId, showEdit, markupContainer, showOrderButtons);
    }

    protected abstract RuServiceHelper<P> getRuServiceHelper();

    protected abstract P getParent();

    protected abstract CrudChildServiceHelper<T> getCrudChildServiceHelperImpl();

    @Override
    protected Collection<T> readDbChildren() {
        return getCrudChildServiceHelperImpl().readDbChildren();
    }

    @Override
    protected void deleteChild(T modelObject) {
        getRuServiceHelper().removeChildAndUpdate(getParent(), getCrudChildServiceHelperImpl(), modelObject);
    }

    @Override
    protected void updateDbChildren(List<T> children) {
        getRuServiceHelper().updateDbEntity(getParent());
    }

    @Override
    protected T createDbChild() {
        T t = getCrudChildServiceHelperImpl().createDbChild();
        getRuServiceHelper().updateDbEntity(getParent());
        return t;
    }

    @Override
    protected <C extends T> C createDbChild(Class<C> createClass) {
        C c = (C) getCrudChildServiceHelperImpl().createDbChild(createClass);
        getRuServiceHelper().updateDbEntity(getParent());
        return c;
    }
}
