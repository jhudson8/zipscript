/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable.format;

import hudson.zipscript.parser.context.ExtendedContext;

public interface Formatter {

	public String format(Object object, ExtendedContext context)
			throws Exception;
}
