/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable.special.object;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.template.element.lang.variable.adapter.RetrievalContext;
import hudson.zipscript.parser.template.element.lang.variable.special.SpecialMethod;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

public class IsSequenceSpecialMethod implements SpecialMethod {

	public static IsSequenceSpecialMethod INSTANCE = new IsSequenceSpecialMethod();

	public Object execute(Object source, RetrievalContext retrievalContext,
			String contextHint, ExtendedContext context) throws Exception {
		return new Boolean(source instanceof Object[]
				|| source instanceof Collection || source instanceof Iterator
				|| source instanceof Enumeration);
	}

	public RetrievalContext getExpectedType() {
		return RetrievalContext.SCALAR;
	}
}
