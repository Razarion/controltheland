package com.btxtech.game.jsre.common;

/**
 * User: beat
 * Date: 18.01.13
 * Time: 17:54
 */
public enum FacebookProducts {
    PRODUCT_1("http://www.razarion.com/fbproducts/CRYST_2000.html", "CRYST_2000", 3.0, 2000),
    PRODUCT_2("http://www.razarion.com/fbproducts/CRYST_4000.html", "CRYST_4000", 5.0, 4000),
    PRODUCT_3("http://www.razarion.com/fbproducts/CRYST_10000.html", "CRYST_10000", 10.0, 10000),
    PRODUCT_4("http://www.razarion.com/fbproducts/CRYST_30000.html", "CRYST_30000", 25.0, 30000),
    PRODUCT_5("http://www.razarion.com/fbproducts/CRYST_70000.html", "CRYST_70000", 50.0, 70000);
    private String productUrl;
    private String shortName;
    private double cost;
    private int crystals;

    FacebookProducts(String productUrl, String shortName, double cost, int crystals) {
        this.productUrl = productUrl;
        this.shortName = shortName;
        this.cost = cost;
        this.crystals = crystals;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public double getCost() {
        return cost;
    }

    public int getCrystals() {
        return crystals;
    }

    public String getShortName() {
        return shortName;
    }

    public static FacebookProducts getProduct4ProductUrl(String productUrl) {
        for (FacebookProducts facebookProducts : values()) {
            if (facebookProducts.getProductUrl().equalsIgnoreCase(productUrl)) {
                return facebookProducts;
            }
        }
        throw new IllegalArgumentException("No Facebook product for URL: " + productUrl);
    }
}
