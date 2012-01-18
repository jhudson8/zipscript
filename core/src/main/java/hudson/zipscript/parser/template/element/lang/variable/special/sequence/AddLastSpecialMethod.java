/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable.special.sequence;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.lang.variable.adapter.RetrievalContext;
import hudson.zipscript.parser.template.element.lang.variable.adapter.SequenceAdapter;

public class AddLastSpecialMethod extends SequenceSpecialMethod {

	private Element element;

	public AddLastSpecialMethod(Element[] vars) {
		this.element = vars[0];
	}

	public Object execute(Object source, SequenceAdapter sequenceAdapter,
			RetrievalContext retrievalContext, String contextHint,
			ExtendedContext context) {
		int length = sequenceAdapter.getSize(source);
		Object value = element.objectValue(context);
		sequenceAdapter.addItemAt(length, value, source);
		return null;
	}
}
