package com.btxtech.game.services.cms.layout;

import com.btxtech.game.services.cms.DbCmsImage;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

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
        DURATION_HH_MM_SS,
        ROUNDED_DOWN_INTEGER
    }

    public enum EditorType {
        PLAIN_TEXT_FILED(true),
        PLAIN_TEXT_AREA(true),
        HTML_AREA(false);

        private boolean escapeHtml;

        private EditorType(boolean escapeHtml) {
            this.escapeHtml = escapeHtml;
        }

        public boolean isEscapeHtml() {
            return escapeHtml;
        }
    }

    private String expression;
    private String springBeanName;
    private EditorType editorType = EditorType.PLAIN_TEXT_FILED;
    private Type optionalType;
    private boolean sortable = false;
    private String sortHintExpression;
    private boolean defaultSortable = false;
    private boolean defaultSortableAsc = false;
    private String sortLinkCssClass;
    private String sortLinkCssClassActive;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbCmsImage sortAscActiveImage;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbCmsImage sortDescActiveImage;
    private String linkCssClass;
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

    public EditorType getEditorType() {
        return editorType;
    }

    public void setEditorType(EditorType editorType) {
        this.editorType = editorType;
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

    public String getLinkCssClass() {
        return linkCssClass;
    }

    public void setLinkCssClass(String linkCssClass) {
        this.linkCssClass = linkCssClass;
    }

    public String getSortLinkCssClass() {
        return sortLinkCssClass;
    }

    public void setSortLinkCssClass(String sortLinkCssClass) {
        this.sortLinkCssClass = sortLinkCssClass;
    }

    public String getSortLinkCssClassActive() {
        return sortLinkCssClassActive;
    }

    public void setSortLinkCssClassActive(String sortLinkCssClassActive) {
        this.sortLinkCssClassActive = sortLinkCssClassActive;
    }

    public DbCmsImage getSortAscActiveImage() {
        return sortAscActiveImage;
    }

    public void setSortAscActiveImage(DbCmsImage sortAscActiveImage) {
        this.sortAscActiveImage = sortAscActiveImage;
    }

    public DbCmsImage getSortDescActiveImage() {
        return sortDescActiveImage;
    }

    public void setSortDescActiveImage(DbCmsImage sortDescActiveImage) {
        this.sortDescActiveImage = sortDescActiveImage;
    }
}
