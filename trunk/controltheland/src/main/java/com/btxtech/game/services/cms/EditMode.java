package com.btxtech.game.services.cms;

/**
 * User: beat
 * Date: 24.06.2011
 * Time: 15:05:22
 */
public class EditMode {
    private String springBeanName;

    public EditMode(String springBeanName) {
        this.springBeanName = springBeanName;
    }

    public String getSpringBeanName() {
        return springBeanName;
    }
}
