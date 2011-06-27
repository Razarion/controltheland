package com.btxtech.game.services.cms;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * User: beat
 * Date: 09.06.2011
 * Time: 12:06:45
 */
@Entity
@DiscriminatorValue("EXPRESSION_PROPERTY")
public class DbExpressionProperty extends DbContent implements DataProviderInfo {
    public enum Type {
        DATE_DDMMYYYY_HH_MM_SS
    };
    private String expression;
    private boolean escapeMarkup = true;
    private Type optionalType;

    @Override
    public String getExpression() {
        return expression;
    }

    @Override
    public void setExpression(String expression) {
        this.expression = expression;
    }

    public void setEscapeMarkup(boolean escapeMarkup) {
        this.escapeMarkup = escapeMarkup;
    }

    public boolean getEscapeMarkup() {
        return escapeMarkup;
    }

    public Type getOptionalType() {
        return optionalType;
    }

    public void setOptionalType(Type optionalType) {
        this.optionalType = optionalType;
    }
}
