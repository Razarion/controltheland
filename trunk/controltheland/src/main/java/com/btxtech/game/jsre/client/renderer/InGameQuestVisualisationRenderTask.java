package com.btxtech.game.jsre.client.renderer;

import com.btxtech.game.jsre.client.cockpit.quest.InGameQuestDirectionVisualisation;
import com.btxtech.game.jsre.client.cockpit.quest.InGameQuestItemVisualisation;
import com.btxtech.game.jsre.client.cockpit.quest.QuestVisualisationModel;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.google.gwt.canvas.dom.client.Context2d;

import java.util.Collection;

/**
 * User: beat
 * Date: 29.07.12
 * Time: 11:46
 */
public class InGameQuestVisualisationRenderTask extends AbstractRenderTask {
    private static final int TERRAIN_HINT_LENGTH = 150;
    private Context2d context2d;

    public InGameQuestVisualisationRenderTask(Context2d context2d) {
        this.context2d = context2d;
    }

    @Override
    public void render(long timeStamp, Collection<SyncItem> itemsInView, Rectangle viewRect, Rectangle tileViewRect) {
        Collection<InGameQuestItemVisualisation> inGameQuestItemVisualisations = QuestVisualisationModel.getInstance().getInGameItemQuestVisualisation(itemsInView, viewRect);
        if (inGameQuestItemVisualisations == null || inGameQuestItemVisualisations.isEmpty()) {
            renderArrow(timeStamp, viewRect);
            return;
        }

        int distance = (TERRAIN_HINT_LENGTH - (int) (TERRAIN_HINT_LENGTH * (timeStamp & 1000) / 1000.0)) / 2;
        for (InGameQuestItemVisualisation inGameQuestItemVisualisation : inGameQuestItemVisualisations) {
            Index relativeMiddle = inGameQuestItemVisualisation.getRelativeMiddlePosition();
            context2d.save();
            context2d.translate(relativeMiddle.getX(), relativeMiddle.getY());
            context2d.drawImage(CanvasElementLibrary.getTlCorner(inGameQuestItemVisualisation.getColor()), -distance, -distance);
            context2d.drawImage(CanvasElementLibrary.getTrCorner(inGameQuestItemVisualisation.getColor()), distance, -distance);
            context2d.drawImage(CanvasElementLibrary.getBlCorner(inGameQuestItemVisualisation.getColor()), -distance, distance);
            context2d.drawImage(CanvasElementLibrary.getBrCorner(inGameQuestItemVisualisation.getColor()), distance, distance);
            context2d.restore();
        }
    }

    private void renderArrow(long timeStamp, Rectangle viewRect) {
        InGameQuestDirectionVisualisation inGameQuestDirectionVisualisation = QuestVisualisationModel.getInstance().getInGameQuestDirectionVisualisationAngel(viewRect);
        if (inGameQuestDirectionVisualisation == null) {
            return;
        }

        context2d.save();
        Index relativeArrowPosition = inGameQuestDirectionVisualisation.getRelativeArrowHotSpot();
        context2d.translate(relativeArrowPosition.getX(), relativeArrowPosition.getY());
        context2d.rotate(-inGameQuestDirectionVisualisation.getAngel());
        int distance = (TERRAIN_HINT_LENGTH - (int) (TERRAIN_HINT_LENGTH * (timeStamp & 500) / 500.0)) / 2;
        context2d.drawImage(CanvasElementLibrary.getArrow(), CanvasElementLibrary.ARROW_WIDTH_TOTAL / 2, distance);
        context2d.restore();

    }
}
