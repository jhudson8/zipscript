/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable.special.number;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.template.element.lang.variable.adapter.RetrievalContext;
import hudson.zipscript.parser.template.element.lang.variable.special.SpecialMethod;

public class CeilingSpecialMethod implements SpecialMethod {

	public static final CeilingSpecialMethod INSTANCE = new CeilingSpecialMethod();

	public Object execute(Object source, RetrievalContext retrievalContext,
			String contextHint, ExtendedContext context) throws Exception {
		return new Long((long) Math.ceil(((Number) source).doubleValue()));
	}

	public RetrievalContext getExpectedType() {
		return RetrievalContext.NUMBER;
	}
}