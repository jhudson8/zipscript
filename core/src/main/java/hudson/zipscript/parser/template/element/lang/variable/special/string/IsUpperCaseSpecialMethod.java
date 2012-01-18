/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable.special.string;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.template.element.lang.variable.adapter.RetrievalContext;
import hudson.zipscript.parser.template.element.lang.variable.special.SpecialMethod;

public class IsUpperCaseSpecialMethod implements SpecialMethod {

	public static final IsUpperCaseSpecialMethod INSTANCE = new IsUpperCaseSpecialMethod();

	public Object execute(Object source, RetrievalContext retrievalContext,
			String contextHint, ExtendedContext context) throws Exception {
		String s = source.toString();
		return new Boolean(s.toUpperCase().equals(s));
	}

	public RetrievalContext getExpectedType() {
		return RetrievalContext.TEXT;
	}
}