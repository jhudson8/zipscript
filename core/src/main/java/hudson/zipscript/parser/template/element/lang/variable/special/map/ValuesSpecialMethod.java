/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable.special.map;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.template.element.lang.variable.adapter.HashAdapter;
import hudson.zipscript.parser.template.element.lang.variable.adapter.RetrievalContext;

public class ValuesSpecialMethod extends MapSpecialMethod {

	public Object execute(Object source, HashAdapter mapAdapter,
			RetrievalContext retrievalContext, String contextHint,
			ExtendedContext context) {
		return mapAdapter.getValues(source);
	}
}
