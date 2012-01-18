/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.util;

import hudson.zipscript.ResourceContainer;
import hudson.zipscript.parser.ExpressionParser;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ParseParameters;
import hudson.zipscript.parser.template.data.ParsingResult;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.DefaultElementFactory;
import hudson.zipscript.parser.template.element.PatternMatcher;
import hudson.zipscript.parser.template.element.lang.TextDefaultElementFactory;
import hudson.zipscript.parser.template.element.lang.variable.SpecialVariableDefaultEelementFactory;
import hudson.zipscript.resource.Resource;
import hudson.zipscript.template.Template;
import hudson.zipscript.template.TemplateImpl;

public class ResourceUtil {

	/**
	 * Load a template
	 * 
	 * @param source
	 *            the source
	 * @param components
	 *            all components for pattern matching
	 * @param patternMatchers
	 *            all pattern matchers
	 * @param parseParameters
	 *            parsing parameters
	 * @return the template
	 * @throws ParseException
	 */
	public static TemplateResource loadTemplate(String source,
			Object resourceLoaderParameter, ParseParameters parseParameters,
			ResourceContainer resourceContainer) throws ParseException {
		Resource resource = resourceContainer.getTemplateResourceLoader()
				.getResource(source, resourceLoaderParameter);
		return loadTemplate(parseParameters, resourceContainer, resource);
	}

	/**
	 * Load a template
	 * 
	 * @param source
	 *            the source
	 * @param components
	 *            all components for pattern matching
	 * @param patternMatchers
	 *            all pattern matchers
	 * @param parseParameters
	 *            parsing parameters
	 * @return the template
	 * @throws ParseException
	 */
	public static TemplateResource loadTemplate(
			ParseParameters parseParameters,
			ResourceContainer resourceContainer, Resource resource)
			throws ParseException {
		return loadTemplate(null, parseParameters, resourceContainer, resource,
				TextDefaultElementFactory.INSTANCE);
	}

	public static Template loadTemplate(String source,
			Object resourceLoaderParameter, PatternMatcher[] patternMatchers,
			ParseParameters parseParameters, ResourceContainer resourceContainer)
			throws ParseException {
		Resource resource = resourceContainer.getEvalResourceLoader()
				.getResource(source, resourceLoaderParameter);
		TemplateResource tr = loadTemplate(patternMatchers, parseParameters,
				resourceContainer, resource,
				SpecialVariableDefaultEelementFactory.INSTANCE);
		tr.template.setResourceContainer(resourceContainer);
		return tr.template;
	}

	/**
	 * Load a template
	 * 
	 * @param source
	 *            the source
	 * @param components
	 *            all components for pattern matching
	 * @param patternMatchers
	 *            all pattern matchers
	 * @param parseParameters
	 *            parsing parameters
	 * @return the template
	 * @throws ParseException
	 */
	private static TemplateResource loadTemplate(
			PatternMatcher[] patternMatchers, ParseParameters parseParameters,
			ResourceContainer resourceContainer, Resource resource,
			DefaultElementFactory defaultElementFactory) throws ParseException {
		String contents = IOUtil.toString(resource.getInputStream());
		;
		ParsingResult pr = null;
		if (null != resourceContainer.getComponents()) {
			pr = ExpressionParser.getInstance().parse(contents,
					resourceContainer.getComponents(), defaultElementFactory,
					0, resourceContainer);
		} else {
			pr = ExpressionParser.getInstance().parse(contents,
					patternMatchers, defaultElementFactory,
					new ParsingSession(parseParameters), 0);
		}
		TemplateImpl template = new TemplateImpl(pr.getElements(), pr
				.getParsingSession(), pr);
		template.setResourceContainer(resourceContainer);
		return new TemplateResource(template, resource);
	}
}
