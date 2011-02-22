/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.test.context.support.AbstractContextLoader;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;

public class WebContextLoader extends AbstractContextLoader {

	protected static final Log logger = LogFactory
			.getLog(WebContextLoader.class);

	public final ConfigurableApplicationContext loadContext(
			final String... locations) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("Loading ApplicationContext for locations ["
							+ StringUtils.arrayToCommaDelimitedString(locations)
							+ "].");
		}

		GenericWebApplicationContext context = new GenericWebApplicationContext();
		customizeBeanFactory(context.getDefaultListableBeanFactory());
		createBeanDefinitionReader(context).loadBeanDefinitions(locations);
		AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
		customizeContext(context);

		context.registerShutdownHook();
		return context;
	}

	protected void customizeBeanFactory(
			final DefaultListableBeanFactory beanFactory) {
		/* no-op */
	}

	protected void customizeContext(final GenericWebApplicationContext context) {
                      /* refresh must be called when customizeContext is overriden */
		context.refresh();
	}

	protected BeanDefinitionReader createBeanDefinitionReader(final GenericApplicationContext context) {
		return new XmlBeanDefinitionReader(context);
	}

	@Override
	public String getResourceSuffix() {
		return "-context.xml";
	}
}