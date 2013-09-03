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
@DiscriminatorValue("BOOLEAN_EXPRESSION_IMAGE")
public class DbContentBooleanExpressionImage extends DbContent implements DataProviderInfo {
    @ManyToOne(fetch = FetchType.LAZY)
    private DbCmsImage trueImage;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbCmsImage falseImage;
    private String expression;
    private String springBeanName;

    public DbCmsImage getTrueImage() {
        return trueImage;
    }

    public void setTrueImage(DbCmsImage trueImage) {
        this.trueImage = trueImage;
    }

    public DbCmsImage getFalseImage() {
        return falseImage;
    }

    public void setFalseImage(DbCmsImage falseImage) {
        this.falseImage = falseImage;
    }

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
}
