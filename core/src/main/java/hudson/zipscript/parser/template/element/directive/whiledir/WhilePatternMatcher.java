/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.directive.whiledir;

import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.directive.AbstractDirectivePatternMatcher;

public class WhilePatternMatcher extends AbstractDirectivePatternMatcher {

	protected Element createElement(char[] startToken, String s,
			int contentIndex, ParsingSession parsingSession)
			throws ParseException {
		return new WhileDirective(s, contentIndex, parsingSession);
	}

	protected String getDirectiveName() {
		return "while";
	}
}