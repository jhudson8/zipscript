/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable.special.sequence;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.template.element.lang.variable.adapter.RetrievalContext;
import hudson.zipscript.parser.template.element.lang.variable.adapter.SequenceAdapter;

public class FirstSpecialMethod extends SequenceSpecialMethod {

	public Object execute(Object source, SequenceAdapter sequenceAdapter,
			RetrievalContext retrievalContext, String contextHint,
			ExtendedContext context) {
		return sequenceAdapter.getItemAt(0, source, retrievalContext,
				contextHint);
	}
}
