package com.btxtech.game.wicket.uiservices;

import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.RuServiceHelper;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

/**
 * User: beat
 * Date: 13.03.2011
 * Time: 23:28:37
 */
public abstract class RuModel<T extends CrudChild> implements IModel<T> {
    private T t;
    private Serializable id;
    private Class<T> clzz;

    protected RuModel(T t, Class<T> clzz) {
        id = t.getId();
        this.clzz = clzz;
    }

    @Override
    public T getObject() {
        if (t == null) {
            t = getRuServiceHelper().readDbChild(id, clzz);
        }
        return t;
    }

    @Override
    public void setObject(T object) {
        // Ignore
    }

    @Override
    public void detach() {
        t = null;
    }

    protected abstract RuServiceHelper<T> getRuServiceHelper();
}
