/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable.special.map;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.template.element.lang.variable.adapter.HashAdapter;
import hudson.zipscript.parser.template.element.lang.variable.adapter.RetrievalContext;
import hudson.zipscript.parser.template.element.lang.variable.special.SpecialMethod;

public abstract class MapSpecialMethod implements SpecialMethod {

	private HashAdapter mapAdapter;

	public final Object execute(Object source,
			RetrievalContext retrievalContext, String contextHint,
			ExtendedContext context) throws Exception {
		if (null == mapAdapter) {
			mapAdapter = context.getResourceContainer()
					.getVariableAdapterFactory().getHashAdapter(source);
		}
		return execute(source, mapAdapter, retrievalContext, contextHint,
				context);
	}

	protected abstract Object execute(Object source, HashAdapter mapAdapter,
			RetrievalContext retrievalContext, String contextHint,
			ExtendedContext context);

	public RetrievalContext getExpectedType() {
		return RetrievalContext.HASH;
	}
}
