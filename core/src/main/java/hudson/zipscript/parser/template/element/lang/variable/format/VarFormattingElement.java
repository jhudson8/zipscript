/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable.format;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ElementIndex;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.lang.IdentifierElement;
import hudson.zipscript.parser.template.element.lang.TextElement;
import hudson.zipscript.parser.template.element.lang.variable.SpecialVariableElementImpl;
import hudson.zipscript.parser.template.element.lang.variable.VariableTokenSeparatorElement;
import hudson.zipscript.parser.template.element.lang.variable.adapter.RetrievalContext;

import java.util.List;



public class VarFormattingElement extends IdentifierElement implements
		VariableTokenSeparatorElement {

	private String format;
	private String formatFunction;
	private Formatter formatter;

	public ElementIndex normalize(int index, List elementList,
			ParsingSession session) throws ParseException {
		if (elementList.size() > index) {
			Element e = (Element) elementList.remove(index);
			if (e instanceof SpecialVariableElementImpl) {
				formatFunction = ((SpecialVariableElementImpl) e)
						.getTokenValue();
			} else if (e instanceof TextElement) {
				this.format = ((TextElement) e).getText();
			}
			return null;
		} else {
			throw new ParseException(this,
					"A formatting element was detected with no value");
		}
	}

	public String toString() {
		if (null != format)
			return "|'" + format + "'";
		else
			return "|" + formatFunction;
	}

	public Object execute(Object source, RetrievalContext retrievalContext,
			String contextHint, ExtendedContext context) {
		if (null == source)
			return null;
		try {
			if (null == formatter) {
				formatter = initializeFormatter(source, context);
			}
			return formatter.format(source, context);
		} catch (Exception e) {
			if (e instanceof ExecutionException) {
				((ExecutionException) e).setElement(this);
				throw (ExecutionException) e;
			} else
				throw new ExecutionException(e.getMessage(), this, e);
		}
	}

	public Object objectValue(ExtendedContext context)
			throws ExecutionException {
		return null;
	}

	protected Formatter initializeFormatter(Object source,
			ExtendedContext context) {
		return context.getResourceContainer().getVariableAdapterFactory().getFormatter(
				this.format, this.formatFunction, source, context);
	}

	public boolean requiresInput() {
		return true;
	}

	public RetrievalContext getExpectedType() {
		return RetrievalContext.SCALAR;
	}
}