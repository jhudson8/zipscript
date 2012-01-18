/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable.special.macroinstance;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.directive.macrodir.MacroInstanceExecutor;
import hudson.zipscript.parser.template.element.lang.WhitespaceElement;
import hudson.zipscript.parser.template.element.lang.variable.adapter.RetrievalContext;
import hudson.zipscript.parser.template.element.lang.variable.special.SpecialMethod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BooleanValueSpecialMethod implements SpecialMethod {

	private Element evaluatorElement;

	public Object execute(Object source, RetrievalContext retrievalContext,
			String contextHint, ExtendedContext context) throws Exception {
		if (null == evaluatorElement) {
			List children = ((MacroInstanceExecutor) source).getMacroInstance()
					.getChildren();
			List nonWhitespaceChildren = new ArrayList(children.size());
			for (Iterator i = children.iterator(); i.hasNext();) {
				Element e = (Element) i.next();
				if (!(e instanceof WhitespaceElement))
					nonWhitespaceChildren.add(e);
			}
			if (nonWhitespaceChildren.size() != 1) {
				throw new ExecutionException("Invalid macro body object value",
						null);
			}
		}
		return new Boolean(evaluatorElement.booleanValue(context));
	}

	public RetrievalContext getExpectedType() {
		return RetrievalContext.BOOLEAN;
	}
}
