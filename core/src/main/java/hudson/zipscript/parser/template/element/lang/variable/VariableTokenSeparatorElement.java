/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.template.element.lang.variable.adapter.RetrievalContext;

public interface VariableTokenSeparatorElement {

	public boolean requiresInput();

	public Object execute(Object source, RetrievalContext retrievalContext,
			String contextHint, ExtendedContext context);

	public RetrievalContext getExpectedType();
}
