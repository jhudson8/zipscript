/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable.special.object;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.template.element.ToStringWithContextElement;
import hudson.zipscript.parser.template.element.lang.variable.adapter.RetrievalContext;
import hudson.zipscript.parser.template.element.lang.variable.special.SpecialMethod;

public class StringSpecialMethod implements SpecialMethod {

	private SpecialMethod stringSpecialMethod;

	public StringSpecialMethod(SpecialMethod stringSpecialMethod) {
		this.stringSpecialMethod = stringSpecialMethod;
	}

	public Object execute(Object source, RetrievalContext retrievalContext,
			String contextHint, ExtendedContext context) throws Exception {
		if (null == source)
			return null;
		else if ((source instanceof String)) {
			if (source instanceof ToStringWithContextElement) {
				source = ((ToStringWithContextElement) source)
						.toString(context);
			} else {
				source = source.toString();
			}
		}
		return stringSpecialMethod.execute(source, retrievalContext,
				contextHint, context);
	}

	public RetrievalContext getExpectedType() {
		return RetrievalContext.SCALAR;
	}
}
