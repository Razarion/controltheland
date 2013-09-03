package com.btxtech.game.jsre.client.dialogs.razarion;

import com.btxtech.game.jsre.client.dialogs.Dialog;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * User: beat
 * Date: 29.07.13
 * Time: 12:34
 */
public abstract class RazarionHelper implements AffordableCallback {
    private String buyTitle;
    private String buyMessage;
    private String buyMessageYesButton;
    private String notEnoughMessage;

    public RazarionHelper(String buyTitle, String buyMessage, String buyMessageYesButton, String notEnoughMessage) {
        this.buyTitle = buyTitle;
        this.buyMessage = buyMessage;
        this.buyMessageYesButton = buyMessageYesButton;
        this.notEnoughMessage = notEnoughMessage;
        askAffordable(this);
    }

    public RazarionHelper(String buyTitle, String notEnoughMessage) {
        this.buyTitle = buyTitle;
        this.notEnoughMessage = notEnoughMessage;
        askAffordable(this);
    }

    @Override
    public void onDetermined(final int razarionCost, final int razarionBalance) {
        if (razarionCost > razarionBalance) {
            DialogManager.showDialog(new HowToGetRazarionPanel(buyTitle, notEnoughMessage, razarionCost, razarionBalance), DialogManager.Type.STACK_ABLE);
        } else {
            if (buyMessage != null) {
                DialogManager.showDialog(new BuyDialogPanel(buyTitle, buyMessage, razarionCost, razarionBalance) {

                    @Override
                    public void init(Dialog dialog) {
                        dialog.setShowYesButton(new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent event) {
                                onBuy(razarionCost, razarionBalance);
                            }
                        }, buyMessageYesButton);
                    }
                }, DialogManager.Type.STACK_ABLE);
            } else {
                Dialog dialog = createBuyDialog(razarionCost, razarionBalance);
                if (dialog != null) {
                    DialogManager.showDialog(createBuyDialog(razarionCost, razarionBalance), DialogManager.Type.STACK_ABLE);
                } else {
                    onBuySilent(razarionCost, razarionBalance);
                }
            }
        }
    }

    protected abstract void askAffordable(AffordableCallback affordableCallback);

    protected void onBuy(int razarionCost, int razarionBalance) {
        throw new UnsupportedOperationException("RazarionHelper.onBuy() must be overridden in subclass when called");
    }

    protected Dialog createBuyDialog(int razarionCost, int razarionBalance) {
        return null;
    }

    protected void onBuySilent(int razarionCost, int razarionBalance) {
        throw new UnsupportedOperationException("RazarionHelper.onBuySilent() must be overridden in subclass when called");
    }

}
