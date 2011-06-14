package com.btxtech.game.services.cms;

import java.util.Collection;

/**
 * User: beat
 * Date: 09.06.2011
 * Time: 12:06:45
 */
public class DbExpressionProperty extends DbProperty {
    private String expression;
    private boolean escapeMarkup = true;

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public void setEscapeMarkup(boolean escapeMarkup) {
        this.escapeMarkup = escapeMarkup;
    }

    public boolean getEscapeMarkup() {
        return escapeMarkup;
    }

    @Override
    public Collection<? extends DbContent> getChildren() {
        return null;
    }
}
