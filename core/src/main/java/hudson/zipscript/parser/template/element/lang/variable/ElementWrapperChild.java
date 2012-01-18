/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.lang.variable.adapter.RetrievalContext;

public class ElementWrapperChild implements VariableChild {

	private hudson.zipscript.parser.template.element.Element element;
	private RetrievalContext retrievalContext;
	private String contextHint;

	public ElementWrapperChild(Element element) {
		this.element = element;
	}

	public Object execute(Object parent, ExtendedContext context)
			throws ExecutionException {
		return element.objectValue(context);
	}

	public String getPropertyName() {
		return element.toString();
	}

	public boolean shouldReturnSomething() {
		return true;
	}

	public String toString() {
		return element.toString();
	}

	public RetrievalContext getRetrievalContext() {
		return retrievalContext;
	}

	public void setRetrievalContext(RetrievalContext retrievalContext) {
		this.retrievalContext = retrievalContext;
	}

	public String getContextHint() {
		return contextHint;
	}

	public void setContextHint(String contextHint) {
		this.contextHint = contextHint;
	}
}
