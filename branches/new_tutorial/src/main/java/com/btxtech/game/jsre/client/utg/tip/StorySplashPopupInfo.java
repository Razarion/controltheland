package com.btxtech.game.jsre.client.utg.tip;

import java.io.Serializable;

/**
 * User: beat
 * Date: 05.09.13
 * Time: 11:12
 */
public class StorySplashPopupInfo implements Serializable {
    public enum ImageType {
        QUEST,
        TICK
    }
    private String title;
    private String mainText;
    private String taskText;
    private ImageType imageType;


    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getMainText() {
        return mainText;
    }

    public void setMainText(String mainText) {
        this.mainText = mainText;
    }

    public String getTaskText() {
        return taskText;
    }

    public void setTaskText(String taskText) {
        this.taskText = taskText;
    }

    public ImageType getImageType() {
        return imageType;
    }

    public void setImageType(ImageType imageType) {
        this.imageType = imageType;
    }
}
