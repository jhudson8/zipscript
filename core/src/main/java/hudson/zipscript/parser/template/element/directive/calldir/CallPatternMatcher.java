/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.directive.calldir;

import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.directive.AbstractDirectivePatternMatcher;

public class CallPatternMatcher extends AbstractDirectivePatternMatcher {

	protected Element createElement(char[] startToken, String s,
			int contentStartTPosition, ParsingSession session)
			throws ParseException {
		return new CallDirective(s, session, contentStartTPosition);
	}

	protected char[] getEndChars() {
		return "/]".toCharArray();
	}

	protected String getDirectiveName() {
		return "call";
	}
}