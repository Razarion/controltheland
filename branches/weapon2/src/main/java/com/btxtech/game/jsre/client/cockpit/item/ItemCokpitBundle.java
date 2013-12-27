package com.btxtech.game.jsre.client.cockpit.item;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface ItemCokpitBundle extends ClientBundle {
    @Source("images/unloadButton-down.png")
    ImageResource unloadButtonDown();
    @Source("images/unloadButton-up.png")
    ImageResource unloadButtonUp();

    @Source("images/launchButton-down.png")
    ImageResource launchButtonDown();
    @Source("images/launchButton-up.png")
    ImageResource launchButtonUp();
    
    @Source("images/upgradeButton-down.png")
    ImageResource upgradeButtonDown();
    @Source("images/upgradeButton-up.png")
    ImageResource upgradeButtonUp();
    @Source("images/upgradeButton-disabled-up.png")
    ImageResource upgradeButtonDisabledUp();
    
    @Source("images/tick-octagon.png")
    ImageResource friend();
    @Source("images/exclamation-red.png")
    ImageResource enemy();
    
    @Source("images/arrowleftup.png")
    ImageResource arrowLeftUp();
    @Source("images/arrowleftdown.png")
    ImageResource arrowLeftDown();

    @Source("images/arrowrightup.png")
    ImageResource arrowRightUp();
    @Source("images/arrowrightdown.png")
    ImageResource arrowRightDown();


}
