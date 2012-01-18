/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable.special.string;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.template.element.lang.variable.adapter.RetrievalContext;
import hudson.zipscript.parser.template.element.lang.variable.special.SpecialMethod;
import hudson.zipscript.parser.util.StringUtil;

public class UpperFirstSpecialMethod implements SpecialMethod {

	public static final UpperFirstSpecialMethod INSTANCE = new UpperFirstSpecialMethod();

	public Object execute(Object source, RetrievalContext retrievalContext,
			String contextHint, ExtendedContext context) throws Exception {
		return StringUtil.firstLetterUpperCase((String) source);
	}

	public RetrievalContext getExpectedType() {
		return RetrievalContext.TEXT;
	}
}