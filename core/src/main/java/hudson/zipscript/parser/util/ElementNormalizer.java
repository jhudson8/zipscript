/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.util;

import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ElementIndex;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.directive.macrodir.MacroInstanceAware;
import hudson.zipscript.parser.template.element.directive.macrodir.MacroInstanceDirective;
import hudson.zipscript.parser.template.element.lang.WhitespaceElement;

import java.util.Iterator;
import java.util.List;

public class ElementNormalizer {

	public static void normalize(List elements, ParsingSession session,
			boolean topLevel) throws ParseException {
		Element e = null;
		ElementIndex ei = null;
		for (int i = 0; i < elements.size(); i++) {
			e = (Element) elements.remove(i);
			if (e instanceof MacroInstanceDirective
					|| e instanceof MacroInstanceAware) {
				session.getNestingStack().push(e);
				ei = e.normalize(i, elements, session);
				if (e != session.getNestingStack().pop()) {
					throw new ParseException(e, "Bad Nesting Stack");
				}
			} else {
				ei = e.normalize(i, elements, session);
			}
			if (null == ei) {
				elements.add(i, e);
			} else {
				if (ei.getIndex() >= 0) {
					elements.add(ei.getIndex(), ei.getElement());
					i = ei.getIndex();
				}
			}
		}

		if (session.getParameters().cleanWhitespace) {
			// remove white spaces
			for (int i = 0; i < elements.size(); i++) {
				if (elements.get(i) instanceof WhitespaceElement) {
					elements.remove(i);
					i--;
				}
			}
		} else if (session.getParameters().trim) {
			trim(elements);
		}

		if (topLevel) {
			session.getNestingStack().clear();
			for (Iterator i = elements.iterator(); i.hasNext();) {
				validate((Element) i.next(), session);
			}
		}
	}

	private static void validate(Element e, ParsingSession session)
			throws ParseException {
		e.validate(session);
		List children = e.getChildren();
		if (null != children) {
			session.getNestingStack().add(e);
			for (Iterator i = children.iterator(); i.hasNext();) {
				Element eSub = (Element) i.next();
				validate(eSub, session);
			}
			Object o = session.getNestingStack().peek();
			if (o == e)
				session.getNestingStack().pop();
			else
				throw new ParseException(e, "Invalid nesting stack");
		}
	}

	public static void trim(List elements) {
		while (elements.size() > 0
				&& elements.get(0) instanceof WhitespaceElement)
			elements.remove(0);
		while (elements.size() > 0
				&& elements.get(elements.size() - 1) instanceof WhitespaceElement)
			elements.remove(elements.size() - 1);
	}
}
