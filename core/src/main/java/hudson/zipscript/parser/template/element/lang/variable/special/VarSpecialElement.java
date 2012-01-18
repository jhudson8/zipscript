/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable.special;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ElementIndex;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.group.GroupElement;
import hudson.zipscript.parser.template.element.lang.IdentifierElement;
import hudson.zipscript.parser.template.element.lang.variable.SpecialVariableElementImpl;
import hudson.zipscript.parser.template.element.lang.variable.VariableTokenSeparatorElement;
import hudson.zipscript.parser.template.element.lang.variable.adapter.RetrievalContext;
import hudson.zipscript.parser.template.element.special.SpecialElement;

import java.util.List;

public class VarSpecialElement extends IdentifierElement implements
		VariableTokenSeparatorElement {

	private SpecialMethod executor;
	private String method;
	private Element[] parameters;

	public VarSpecialElement() {
	}

	public VarSpecialElement(SpecialMethod executor) {
		this.executor = executor;
	}

	public ElementIndex normalize(int index, List elementList,
			ParsingSession session) throws ParseException {
		if (elementList.size() > index) {
			Element e = (Element) elementList.remove(index);
			e.normalize(index, elementList, session);
			if (e instanceof SpecialVariableElementImpl)
				method = ((SpecialVariableElementImpl) e).getTokenValue();
			else if (e instanceof SpecialElement)
				method = ((SpecialElement) e).getTokenValue();
			// see if we've got parameters
			if (elementList.size() > index) {
				if (elementList.get(index) instanceof GroupElement) {
					GroupElement parameters = (GroupElement) elementList
							.remove(index);
					parameters.normalize(index, elementList, session);
					this.parameters = (Element[]) parameters
							.getChildren()
							.toArray(
									new Element[parameters.getChildren().size()]);
				}
			}
			executor = session.getResourceContainer()
					.getVariableAdapterFactory().getSpecialMethod(method,
							parameters, session, this);
			if (null == executor)
				throw new ExecutionException("Unknown special method '"
						+ method + "'", null);
			return null;
		} else {
			throw new ParseException(this,
					"Default element detected with no value");
		}
	}

	public Object execute(Object source, RetrievalContext retrievalContext,
			String contextHint, ExtendedContext context) {
		try {
			if (null == source)
				return null;
			return executor.execute(source, retrievalContext, contextHint,
					context);
		} catch (Exception e) {
			if (e instanceof ExecutionException) {
				((ExecutionException) e).setElement(this);
				throw (ExecutionException) e;
			} else
				throw new ExecutionException(e.getMessage(), this, e);
		}
	}

	public String toString() {
		return "?" + executor.toString();
	}

	public boolean requiresInput() {
		return true;
	}

	public RetrievalContext getExpectedType() {
		return executor.getExpectedType();
	}
}