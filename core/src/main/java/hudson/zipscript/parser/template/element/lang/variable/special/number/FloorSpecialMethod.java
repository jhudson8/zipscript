/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable.special.number;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.template.element.lang.variable.adapter.RetrievalContext;
import hudson.zipscript.parser.template.element.lang.variable.special.SpecialMethod;

public class FloorSpecialMethod implements SpecialMethod {

	public static final FloorSpecialMethod INSTANCE = new FloorSpecialMethod();

	public Object execute(Object source, RetrievalContext retrievalContext,
			String contextHint, ExtendedContext context) throws Exception {
		return new Long((long) Math.floor(((Number) source).doubleValue()));
	}

	public RetrievalContext getExpectedType() {
		return RetrievalContext.NUMBER;
	}
}