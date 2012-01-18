/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.special;

import hudson.zipscript.ext.data.DefaultElementContainer;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.DefaultElementFactory;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.lang.variable.SpecialVariableElementImpl;

public class NoMapDefaultVariablePatternMatcher implements DefaultElementFactory {

	private static NoMapDefaultVariablePatternMatcher INSTANCE = new NoMapDefaultVariablePatternMatcher();

	public static NoMapDefaultVariablePatternMatcher getInstance() {
		return INSTANCE;
	}

	public DefaultElementContainer createDefaultElement(
			Element nextElement, String text, ParsingSession session,
			int contentPosition) throws ParseException {
		return new DefaultElementContainer(
			new SpecialVariableElementImpl(text, session, contentPosition, false),
			nextElement);
	}

	public boolean doAppend(char c) {
		return true;
	}
}