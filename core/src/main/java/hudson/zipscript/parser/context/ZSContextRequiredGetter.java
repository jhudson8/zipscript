/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.context;

import hudson.zipscript.parser.template.element.lang.variable.adapter.RetrievalContext;

public interface ZSContextRequiredGetter {

	public Object get(String key, RetrievalContext retrievalContext,
			String contextHint, ExtendedContext context);
}
