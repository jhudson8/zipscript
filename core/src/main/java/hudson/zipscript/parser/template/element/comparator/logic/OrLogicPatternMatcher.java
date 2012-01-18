/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.comparator.logic;

import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.PatternMatcher;

import java.nio.CharBuffer;
import java.util.List;

public class OrLogicPatternMatcher implements PatternMatcher {

	public char[] getStartToken() {
		return new char[] { '|', '|' };
	}

	public char[][] getStartTokens() {
		return null;
	}

	public Element match(char previousChar, char[] startChars,
			CharBuffer reader, ParsingSession parseData, List elements,
			StringBuffer unmatchedChars) throws ParseException {
		return new OrLogicElement();
	}
}
