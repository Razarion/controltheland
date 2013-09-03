package com.btxtech.game.jsre.regioneditor.images;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Images extends ClientBundle {

    ImageResource eraser();

    @Source("magnifier-zoom-in.png")
    ImageResource magnifierZoomIn();

    @Source("magnifier-zoom-out.png")
    ImageResource magnifierZoomOut();

    @Source("paint-brush.png")
    ImageResource paintBrush();

    @Source("big-cursor.png")
    ImageResource bigCursor();

    @Source("middle-cursor.png")
    ImageResource middleCursor();

    @Source("small-cursor.png")
    ImageResource smallCursor();

    ImageResource save();

}
