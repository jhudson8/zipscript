/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.comparator;

import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.PatternMatcher;

import java.nio.CharBuffer;
import java.util.List;

public class ComparatorPatternMatcher implements PatternMatcher {

	public char[] getStartToken() {
		return null;
	}

	public char[][] getStartTokens() {
		return new char[][] { ">".toCharArray(), "<".toCharArray(),
				"==".toCharArray(), "!=".toCharArray() };
	}

	public Element match(char previousChar, char[] startChars,
			CharBuffer reader, ParsingSession parseData, List elements,
			StringBuffer unmatchedChars) throws ParseException {
		if (startChars.length == 2) {
			// we have the full pattern
			if (startChars[0] == '=')
				return new EqExpression();
			else
				return new NEqExpression();
		} else {
			char c = Character.MIN_VALUE;
			if (reader.hasRemaining())
				c = reader.charAt(0);
			if (c == '=') {
				reader.get();
				if (startChars[0] == '<')
					return new LEqExpression();
				else
					return new GEqExpression();
			} else {
				c = startChars[0];
				if (c == '<')
					return new LtExpression();
				else
					return new GtrExpression();
			}
		}
	}
}