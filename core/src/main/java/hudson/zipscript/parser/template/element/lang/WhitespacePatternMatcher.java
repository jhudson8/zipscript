/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang;

import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.PatternMatcher;

import java.nio.CharBuffer;
import java.util.List;

public class WhitespacePatternMatcher implements PatternMatcher {

	public char[] getStartToken() {
		return null;
	}

	public char[][] getStartTokens() {
		return new char[][] { " ".toCharArray(), "\r\n".toCharArray(),
				"\n".toCharArray(), "\t".toCharArray() };
	}

	public Element match(char previousChar, char[] startChars,
			CharBuffer reader, ParsingSession session, List elements,
			StringBuffer unmatchedChars) throws ParseException {
		return new WhitespaceElementImpl();
	}
}
