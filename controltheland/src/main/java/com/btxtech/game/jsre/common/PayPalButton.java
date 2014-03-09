package com.btxtech.game.jsre.common;

/**
 * User: beat
 * Date: 18.01.13
 * Time: 17:54
 */
public enum PayPalButton {
    B_1("PE44JA3J2AZ2J", "UDY49SFNKAJJE", "CRYST_2000", 3.0, 2000),
    B_2("T6UDKDH59Y43E", "QR8RUMJM2AFQ2", "CRYST_4000", 5.0, 4000),
    B_3("7LSHFG9LM88VL", "N2B23T28DUD8A", "CRYST_10000", 10.0, 10000),
    B_4("YLVYNLXBSJXGY", "8PCH73BN969N6", "CRYST_30000", 25.0, 30000),
    B_5("YLVYNLXBSJXGY", "DCT6H5J9PBAP6", "CRYST_70000", 50.0, 70000); // TODO fix hostedButtonId
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
