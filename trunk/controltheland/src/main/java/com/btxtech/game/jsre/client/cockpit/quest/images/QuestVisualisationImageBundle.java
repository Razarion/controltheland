package com.btxtech.game.jsre.client.cockpit.quest.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface QuestVisualisationImageBundle extends ClientBundle {
    public static final QuestVisualisationImageBundle INSTANCE = GWT.create(QuestVisualisationImageBundle.class); 

    ImageResource exclamation();

    ImageResource tick();

    @Source("startmission-down.png")
    ImageResource startmissionDown();

    @Source("startmission-up.png")
    ImageResource startmissionUp();

}
