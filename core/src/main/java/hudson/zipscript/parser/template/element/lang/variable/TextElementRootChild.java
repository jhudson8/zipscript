/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.template.element.lang.TextElement;
import hudson.zipscript.parser.template.element.lang.variable.adapter.RetrievalContext;

public class TextElementRootChild implements VariableChild {

	private TextElement textElement;
	private RetrievalContext retrievalContext;
	private String contextHint;

	public TextElementRootChild(TextElement textElement) {
		this.textElement = textElement;
	}

	public Object execute(Object parent, ExtendedContext context) {
		return textElement.objectValue(context);
	}

	public boolean shouldReturnSomething() {
		return true;
	}

	public String toString() {
		return textElement.toString();
	}

	public String getPropertyName() {
		return textElement.toString();
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

	public TextElement getTextElement () {
		return textElement;
	}
}