/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.directive.macrodir;

import hudson.zipscript.parser.context.ExtendedContext;

import java.util.List;
import java.util.Map;

public interface MacroInstanceAware {

	/**
	 * Return all template defined parameters
	 * 
	 * @param context
	 *            the context
	 * @param list
	 *            list to add macros to
	 * @param macro
	 *            the reference macro
	 * @param additionalContextEntries
	 *            map for entries that should be added to the context
	 */
	public void getMatchingTemplateDefinedParameters(ExtendedContext context,
			List list, MacroDirective macro, Map additionalContextEntries);
}
