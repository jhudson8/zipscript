/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.util;

import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.ElementAttribute;
import hudson.zipscript.parser.template.element.directive.macrodir.MacroOrientedElement;
import hudson.zipscript.parser.template.element.lang.AssignmentElement;
import hudson.zipscript.parser.template.element.lang.TextElement;
import hudson.zipscript.parser.template.element.special.SpecialStringElement;

import java.util.List;

public class AttributeUtil {

	public static ElementAttribute getNamedAttribute(List elements,
			ParsingSession parsingSession, Element element)
			throws ParseException {
		String name = null;
		Element e;
		e = (Element) elements.remove(0);
		if (e instanceof SpecialStringElement)
			name = ((SpecialStringElement) e).getTokenValue();
		else if (e instanceof TextElement)
			name = ((TextElement) e).getText();
		else
			throw new ParseException(element,
					"Unexpected element, expecting macro attribute name.  Found '"
							+ e + "'");
		// validate name
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (!(Character.isLetterOrDigit(c) || c == '_' || c == '-'))
				throw new ParseException(element,
						"Invalid macro attribute name '" + name + "'");
		}

		// attribute properties
		if (elements.size() > 0) {
			e = (Element) elements.get(0);
			if (e instanceof AssignmentElement) {
				elements.remove(0);
				if (elements.size() == 0) {
					throw new ParseException(element, "Unexpected content '"
							+ e + "'");
				} else {
					// value
					e = (Element) elements.remove(0);
					if (e instanceof AssignmentElement) {
						throw new ParseException(element,
								"Unexpected content '" + e + "'");
					} else {
						return new ElementAttribute(name, e);
					}
				}
			} else {
				throw new ParseException(element,
						"Unexpected element, expecting '='.  Found '" + e + "'");
			}
		} else {
			throw new ParseException(element, "Missing macro value for '"
					+ name + "' in " + element.toString());
		}
	}

	public static ElementAttribute getAttribute(List elements,
			ParsingSession session, MacroOrientedElement element)
			throws ParseException {
		if (elements.size() == 0)
			return null;

		if (element.isOrdinal()) {
			Element e = (Element) elements.remove(0);
			if (e instanceof AssignmentElement)
				throw new ParseException(element,
						"Unexpected token '=' found when parsing ordinal macro attributes");
			return new ElementAttribute(null, e);
		} else {
			return getNamedAttribute(elements, session, element);
		}
	}
}
