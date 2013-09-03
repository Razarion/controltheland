package com.btxtech.game.services.cms.layout;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * User: beat
 * Date: 06.07.2011
 * Time: 12:06:45
 */
@Entity
@DiscriminatorValue("ACTION_BUTTON")
public class DbContentActionButton extends DbContent implements DataProviderInfo {
    private String springBeanName;
    private String methodName;
    private String parameterExpression;
    private String leftSideSpringBeanName;
    private String leftSideOperandExpression;
    private String rightSideOperandExpression;
    private String unfilledHtml;
    private boolean unfilledHtmlEscapeMarkup;

    public String getUnfilledHtml() {
        return unfilledHtml;
    }

    public void setUnfilledHtml(String unfilledHtml) {
        this.unfilledHtml = unfilledHtml;
    }

    public void setUnfilledHtmlEscapeMarkup(boolean unfilledHtmlEscapeMarkup) {
        this.unfilledHtmlEscapeMarkup = unfilledHtmlEscapeMarkup;
    }

    public boolean getUnfilledHtmlEscapeMarkup() {
        return unfilledHtmlEscapeMarkup;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getParameterExpression() {
        return parameterExpression;
    }

    public void setParameterExpression(String parameterExpression) {
        this.parameterExpression = parameterExpression;
    }

    @Override
    public String getSpringBeanName() {
        return springBeanName;
    }

    @Override
    public void setSpringBeanName(String springBeanName) {
        this.springBeanName = springBeanName;
    }

    public String getLeftSideSpringBeanName() {
        return leftSideSpringBeanName;
    }

    public void setLeftSideSpringBeanName(String leftSideSpringBeanName) {
        this.leftSideSpringBeanName = leftSideSpringBeanName;
    }

    public String getLeftSideOperandExpression() {
        return leftSideOperandExpression;
    }

    public String getRightSideOperandExpression() {
        return rightSideOperandExpression;
    }

    public void setLeftSideOperandExpression(String leftSideOperandExpression) {
        this.leftSideOperandExpression = leftSideOperandExpression;
    }

    public void setRightSideOperandExpression(String rightSideOperandExpression) {
        this.rightSideOperandExpression = rightSideOperandExpression;
    }
}
