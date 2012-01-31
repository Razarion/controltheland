package com.btxtech.game.services.cms.layout;

import com.btxtech.game.services.cms.DbCmsImage;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

/**
 * User: beat
 * Date: 25.07.2011
 * Time: 13:37:04
 */
@Entity
@DiscriminatorValue("START_LEVEL_TASK_BUTTON")
public class DbContentStartLevelTaskButton extends DbContent implements DataProviderInfo {
    @ManyToOne(fetch = FetchType.LAZY)
    private DbCmsImage startImage;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbCmsImage doneImage;
    private String expression;
    private String doneExpression;

    public DbCmsImage getStartImage() {
        return startImage;
    }

    public void setStartImage(DbCmsImage startImage) {
        this.startImage = startImage;
    }

    public DbCmsImage getDoneImage() {
        return doneImage;
    }

    public void setDoneImage(DbCmsImage doneImage) {
        this.doneImage = doneImage;
    }

    @Override
    public String getExpression() {
        return expression;
    }

    @Override
    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getDoneExpression() {
        return doneExpression;
    }

    public void setDoneExpression(String doneExpression) {
        this.doneExpression = doneExpression;
    }
}
