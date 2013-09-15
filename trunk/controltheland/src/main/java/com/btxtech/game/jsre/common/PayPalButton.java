package com.btxtech.game.jsre.common;

/**
 * User: beat
 * Date: 18.01.13
 * Time: 17:54
 */
public enum PayPalButton {
    B_1000("PE44JA3J2AZ2J", "UDY49SFNKAJJE", "RAZ1000", 5.0, 1000),
    B_2200("T6UDKDH59Y43E", "QR8RUMJM2AFQ2", "RAZ2200", 10.0, 2200),
    B_4600("7LSHFG9LM88VL", "8PCH73BN969N6", "RAZ4600", 20.0, 4600),
    B_12500("YLVYNLXBSJXGY", "N2B23T28DUD8A", "RAZ12500", 50.0, 12500);
    private String hostedButtonId;
    private String sandboxHostedButtonId;
    private String itemNumber;
    private double cost;
    private int crystals;

    PayPalButton(String hostedButtonId, String sandboxHostedButtonId, String itemNumber, double cost, int crystals) {
        this.hostedButtonId = hostedButtonId;
        this.sandboxHostedButtonId = sandboxHostedButtonId;
        this.itemNumber = itemNumber;
        this.cost = cost;
        this.crystals = crystals;
    }

    public String getHostedButtonId() {
        if (PayPalUtils.IS_SANDBOX) {
            return sandboxHostedButtonId;
        } else {
            return hostedButtonId;
        }
    }

    public static PayPalButton getButton4ItemNumber(String itemNumber) {
        for (PayPalButton payPalButton : values()) {
            if (payPalButton.itemNumber.equals(itemNumber)) {
                return payPalButton;
            }
        }
        throw new IllegalArgumentException("No PayPalButton for: " + itemNumber);
    }

    public double getCost() {
        return cost;
    }

    public String getItemNumber() {
        return itemNumber;
    }

    public int getCrystals() {
        return crystals;
    }
}
