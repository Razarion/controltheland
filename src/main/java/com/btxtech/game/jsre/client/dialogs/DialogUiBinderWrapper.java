package com.btxtech.game.jsre.client.dialogs;

import com.google.gwt.user.client.ui.Composite;

/**
 * User: beat
 * Date: 02.06.13
 * Time: 11:53
 */
abstract public class DialogUiBinderWrapper extends Composite {
    private Dialog dialog;

    public abstract String getDialogTitle();

    public void init(Dialog dialog){

    }

    public void setAndInit(Dialog dialog) {
        this.dialog = dialog;
        init(dialog);
    }

    public void close() {
        dialog.hide(true);
    }
}
