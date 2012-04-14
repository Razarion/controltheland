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
@DiscriminatorValue("ACTIVATE_QUEST_BUTTON")
public class DbContentActivateQuestButton extends DbContent implements DataProviderInfo {
    @ManyToOne(fetch = FetchType.LAZY)
    private DbCmsImage startImage;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbCmsImage doneImage;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbCmsImage abortImage;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbCmsImage blockedImage;
    private String expression;
    private String doneExpression;
    private String activeExpression;
    private String blockedExpression;

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

    public DbCmsImage getAbortImage() {
        return abortImage;
    }

    public void setAbortImage(DbCmsImage abortImage) {
        this.abortImage = abortImage;
    }

    public DbCmsImage getBlockedImage() {
        return blockedImage;
    }

    public void setBlockedImage(DbCmsImage blockedImage) {
        this.blockedImage = blockedImage;
    }

    public String getActiveExpression() {
        return activeExpression;
    }

    public void setActiveExpression(String activeExpression) {
        this.activeExpression = activeExpression;
    }

    public String getBlockedExpression() {
        return blockedExpression;
    }

    public void setBlockedExpression(String blockedExpression) {
        this.blockedExpression = blockedExpression;
    }
}
