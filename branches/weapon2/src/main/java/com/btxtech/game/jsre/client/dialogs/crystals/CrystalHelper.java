package com.btxtech.game.jsre.client.dialogs.crystals;

import com.btxtech.game.jsre.client.dialogs.Dialog;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * User: beat
 * Date: 29.07.13
 * Time: 12:34
 */
public abstract class CrystalHelper implements AffordableCallback {
    private String buyTitle;
    private String buyMessage;
    private String buyMessageYesButton;
    private String notEnoughMessage;

    public CrystalHelper(String buyTitle, String buyMessage, String buyMessageYesButton, String notEnoughMessage) {
        this.buyTitle = buyTitle;
        this.buyMessage = buyMessage;
        this.buyMessageYesButton = buyMessageYesButton;
        this.notEnoughMessage = notEnoughMessage;
        askAffordable(this);
    }

    public CrystalHelper(String buyTitle, String notEnoughMessage) {
        this.buyTitle = buyTitle;
        this.notEnoughMessage = notEnoughMessage;
        askAffordable(this);
    }

    @Override
    public void onDetermined(final int crystalCost, final int crystalBalance) {
        if (crystalCost > crystalBalance) {
            DialogManager.showDialog(new HowToGetCrystalsPanel(buyTitle, notEnoughMessage, crystalCost, crystalBalance), DialogManager.Type.STACK_ABLE);
        } else {
            if (buyMessage != null) {
                DialogManager.showDialog(new BuyDialogPanel(buyTitle, buyMessage, crystalCost, crystalBalance) {

                    @Override
                    public void init(Dialog dialog) {
                        dialog.setShowYesButton(new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent event) {
                                onBuy(crystalCost, crystalBalance);
                            }
                        }, buyMessageYesButton);
                    }
                }, DialogManager.Type.STACK_ABLE);
            } else {
                Dialog dialog = createBuyDialog(crystalCost, crystalBalance);
                if (dialog != null) {
                    DialogManager.showDialog(createBuyDialog(crystalCost, crystalBalance), DialogManager.Type.STACK_ABLE);
                } else {
                    onBuySilent(crystalCost, crystalBalance);
                }
            }
        }
    }

    protected abstract void askAffordable(AffordableCallback affordableCallback);

    protected void onBuy(int crystalCost, int crystalBalance) {
        throw new UnsupportedOperationException("CrystalHelper.onBuy() must be overridden in subclass when called");
    }

    protected Dialog createBuyDialog(int crystalCost, int crystalBalance) {
        return null;
    }

    protected void onBuySilent(int crystalCost, int crystalBalance) {
        throw new UnsupportedOperationException("CrystalHelper.onBuySilent() must be overridden in subclass when called");
    }

}
