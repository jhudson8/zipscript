/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable.special.string;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.template.element.lang.variable.adapter.RetrievalContext;
import hudson.zipscript.parser.template.element.lang.variable.special.SpecialMethod;

public class LowerCaseSpecialMethod implements SpecialMethod {

	public static final LowerCaseSpecialMethod INSTANCE = new LowerCaseSpecialMethod();

	public Object execute(Object source, RetrievalContext retrievalContext,
			String contextHint, ExtendedContext context) throws Exception {
		return source.toString().toLowerCase();
	}

	public RetrievalContext getExpectedType() {
		return RetrievalContext.TEXT;
	}
}