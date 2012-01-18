/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.special;

import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.PatternMatcher;

import java.nio.CharBuffer;
import java.util.List;

public class BooleanPatternMatcher implements PatternMatcher {

	public char[] getStartToken() {
		return null;
	}

	public char[][] getStartTokens() {
		return new char[][] { "true".toCharArray(), "false".toCharArray() };
	}

	public Element match(char previousChar, char[] startChars,
			CharBuffer reader, ParsingSession session, List elements,
			StringBuffer unmatchedChars) throws ParseException {
		if (reader.hasRemaining()) {
			char c = reader.charAt(0);
			if (Character.isLetterOrDigit(c) || c == '_')
				return null;
		}
		if (startChars[0] == 't')
			return new BooleanElement(true);
		else
			return new BooleanElement(false);
	}

}
