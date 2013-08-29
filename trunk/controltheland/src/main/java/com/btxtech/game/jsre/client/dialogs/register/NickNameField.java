package com.btxtech.game.jsre.client.dialogs.register;

import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.VerificationRequestCallback;
import com.btxtech.game.jsre.client.VerificationRequestField;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Created with IntelliJ IDEA.
 * User: beat
 * Date: 27.12.12
 * Time: 12:57
 */
public class NickNameField extends VerificationRequestField {


    public NickNameField(ValidListener validListener) {
        super(validListener);
    }

    @Override
    protected void checkNameRequest(String name, final VerificationRequestCallback verificationRequestCallback) {
        if (Connection.getMovableServiceAsync() != null) {
            Connection.getMovableServiceAsync().isNickNameValid(getText(), new AsyncCallback<VerificationRequestCallback.ErrorResult>() {
                @Override
                public void onFailure(Throwable caught) {
                    ClientExceptionHandler.handleException("MovableServiceAsync.isNickNameValid()", caught);
                }

                @Override
                public void onSuccess(VerificationRequestCallback.ErrorResult errorResult) {
                    verificationRequestCallback.onResponse(errorResult);
                }
            });
        }
    }
}
