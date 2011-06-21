package com.btxtech.game.services.cms;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Collection;

/**
 * User: beat
 * Date: 09.06.2011
 * Time: 12:06:45
 */
@Entity
@DiscriminatorValue("EXPRESSION_PROPERTY")
public class DbExpressionProperty extends DbContent {
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
}
