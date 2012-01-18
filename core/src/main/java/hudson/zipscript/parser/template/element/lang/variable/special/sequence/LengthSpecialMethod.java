/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable.special.sequence;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.template.element.lang.variable.adapter.HashAdapter;
import hudson.zipscript.parser.template.element.lang.variable.adapter.RetrievalContext;
import hudson.zipscript.parser.template.element.lang.variable.adapter.SequenceAdapter;
import hudson.zipscript.parser.template.element.lang.variable.special.SpecialMethod;

public class LengthSpecialMethod implements SpecialMethod {

	private SequenceAdapter sequenceAdapter = null;
	private HashAdapter hashAdapter = null;
	private boolean alwaysCheck = false;

	public Object execute(Object source, RetrievalContext retrievalContext,
			String contextHint, ExtendedContext context) throws Exception {
		if (alwaysCheck || (null == sequenceAdapter && null == hashAdapter)) {
			sequenceAdapter = context.getResourceContainer().getVariableAdapterFactory().getSequenceAdapter(source);
			if (null == sequenceAdapter) {
				hashAdapter = context.getResourceContainer().getVariableAdapterFactory().getHashAdapter(source);
				if (null == hashAdapter)
					throw new ExecutionException("Invalid object for ?length '" + source.getClass().getName() + "'", null);
			}
		}
		try {
			if (null != sequenceAdapter)
				return new Integer(sequenceAdapter.getSize(source));
			else
				return new Integer(hashAdapter.getSize(source));
		}
		catch (ClassCastException e) {
			this.alwaysCheck = true;
			return this.execute(source, retrievalContext, contextHint, context);
		}
	}

	public RetrievalContext getExpectedType() {
		return RetrievalContext.UNKNOWN;
	}
}