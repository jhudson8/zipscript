/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.group;

import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.PatternMatcher;

import java.nio.CharBuffer;
import java.util.List;

public class GroupPatternMatcher implements PatternMatcher {

	public char[][] getStartTokens() {
		return new char[][] { "(".toCharArray(), ")".toCharArray() };
	}

	public char[] getStartToken() {
		return null;
	}

	public Element match(char previousChar, char[] startChars,
			CharBuffer contents, ParsingSession session, List elements,
			StringBuffer unmatchedChars) throws ParseException {
		if (startChars[0] == '(')
			return new GroupElement();
		else
			return new EndGroupElement();
	}
}
