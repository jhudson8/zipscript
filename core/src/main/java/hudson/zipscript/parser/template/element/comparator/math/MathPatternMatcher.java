/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.comparator.math;

import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.PatternMatcher;

import java.nio.CharBuffer;
import java.util.List;

public class MathPatternMatcher implements PatternMatcher {

	public char[] getStartToken() {
		return null;
	}

	public char[][] getStartTokens() {
		return new char[][] { "+".toCharArray(), "-".toCharArray(),
				"*".toCharArray(), "/".toCharArray(), "%".toCharArray(),
				"^".toCharArray() };
	}

	public Element match(char previousChar, char[] startChars,
			CharBuffer reader, ParsingSession parseData, List elements,
			StringBuffer unmatchedChars) throws ParseException {
		char c = startChars[0];
		if (c == '+')
			return new AddExpression();
		else if (c == '-')
			return new SubtractExpression();
		else if (c == '*')
			return new MultiplyExpression();
		else if (c == '/')
			return new DivideExpression();
		else if (c == '%')
			return new ModExpression();
		else
			return new ExponentExpression();
	}
}
