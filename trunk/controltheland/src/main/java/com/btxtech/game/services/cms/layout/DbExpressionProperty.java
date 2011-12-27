package com.btxtech.game.services.cms.layout;

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
        DATE_DDMMYYYY_HH_MM_SS,
        DURATION_HH_MM_SS
    }

    private String expression;
    private String springBeanName;
    private boolean escapeMarkup = true;
    private Type optionalType;
    private boolean sortable = false;
    private String sortHintExpression;
    private boolean defaultSortable = false;
    private boolean defaultSortableAsc = false;
    private boolean link;

    @Override
    public String getExpression() {
        return expression;
    }

    @Override
    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Override
    public String getSpringBeanName() {
        return springBeanName;
    }

    @Override
    public void setSpringBeanName(String springBeanName) {
        this.springBeanName = springBeanName;
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

    public boolean isSortable() {
        return sortable;
    }

    public void setSortable(boolean sortable) {
        this.sortable = sortable;
    }

    public String getSortHintExpression() {
        return sortHintExpression;
    }

    public void setSortHintExpression(String sortHintExpression) {
        this.sortHintExpression = sortHintExpression;
    }

    public boolean isDefaultSortable() {
        return defaultSortable;
    }

    public void setDefaultSortable(boolean defaultSortable) {
        this.defaultSortable = defaultSortable;
    }

    public boolean isDefaultSortableAsc() {
        return defaultSortableAsc;
    }

    public void setDefaultSortableAsc(boolean defaultSortableAsc) {
        this.defaultSortableAsc = defaultSortableAsc;
    }


    public boolean isLink() {
        return link;
    }

    public void setLink(boolean link) {
        this.link = link;
    }
}
