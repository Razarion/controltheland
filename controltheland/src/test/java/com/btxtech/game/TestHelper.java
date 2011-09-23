package com.btxtech.game;

import java.io.File;
import java.net.URISyntaxException;

/**
 * User: beat
 * Date: 23.09.2011
 * Time: 11:57:31
 */
public abstract class TestHelper {
    public static void printJarLocation(Class clazz) {
        try {
            File jarFile = new File(clazz.getProtectionDomain().getCodeSource().getLocation().toURI());
            System.out.println(jarFile);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
