/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable.special.sequence;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.lang.variable.adapter.RetrievalContext;
import hudson.zipscript.parser.template.element.lang.variable.adapter.SequenceAdapter;

public class ContainsSpecialMethod extends SequenceSpecialMethod {

	private Element checkElement;

	public ContainsSpecialMethod(Element[] vars) {
		if (null != vars && vars.length > 0) {
			checkElement = vars[0];
		}
	}

	public Object execute(Object source, SequenceAdapter sequenceAdapter,
			RetrievalContext retrievalContext, String contextHint,
			ExtendedContext context) {
		Object check = checkElement.objectValue(context);
		if (null == check)
			return Boolean.FALSE;
		if (source instanceof String) {
			// special case
			return new Boolean(((String) source).indexOf(check.toString()) > 0);
		} else {
			return new Boolean(sequenceAdapter.contains(check, source));
		}
	}
}