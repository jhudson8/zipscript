/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.special;

import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.AbstractPatternMatcher;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.lang.TextElement;

import java.nio.CharBuffer;
import java.util.List;

public class StringPatternMatcher extends AbstractPatternMatcher {

	protected Element createElement(char[] startToken, String s,
			int contentIndex, ParsingSession session) {
		return new TextElement(s, true);
	}

	public Element match(char previousChar, char[] startChars,
			CharBuffer contents, ParsingSession parseData, List elements,
			StringBuffer unmatchedChars) throws ParseException {
		int position = contents.position();
		int length = findMatch(contents, startChars, startChars,
				getInvalidChars(), true);
		String s = read(contents, position, length, 1);
		Element rtn = createElement(startChars, s, contents.position(),
				parseData);
		rtn = onCreateElement(rtn, startChars, s, contents.position(), contents);
		return rtn;
	}

	public char[] getStartToken() {
		return null;
	}

	public char[][] getStartTokens() {
		return new char[][] { "\'".toCharArray(), "\"".toCharArray() };
	}

	protected char[] getEndChars() {
		return null;
	}

	protected boolean allowEmpty() {
		return true;
	}
}
