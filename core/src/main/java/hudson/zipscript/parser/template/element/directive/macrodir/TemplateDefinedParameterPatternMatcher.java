/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.directive.macrodir;

import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.PatternMatcher;
import hudson.zipscript.parser.util.StringUtil;

import java.nio.CharBuffer;
import java.util.List;

public class TemplateDefinedParameterPatternMatcher implements PatternMatcher {

	public char[] getStartToken() {
		return null;
	}

	public char[][] getStartTokens() {
		return new char[][] { "[.%".toCharArray(), "[%".toCharArray() };
	}

	public Element match(char previousChar, char[] startChars,
			CharBuffer reader, ParsingSession session, List elements,
			StringBuffer unmatchedChars) throws ParseException {
		if (StringUtil.isEscaped(unmatchedChars))
			return null;
		boolean isFlat = false;
		int nesting = 1;
		int startPos = reader.position();
		previousChar = Character.MIN_VALUE;
		StringBuffer sb = new StringBuffer();
		while (reader.hasRemaining()) {
			char c = reader.get();
			boolean inString = false;
			if (c == '[') {
				if (!inString)
					nesting++;
			} else if (c == '\'' || c == '\"') {
				inString = !inString;
			} else if (c == ']') {
				if (!inString)
					nesting--;
				if (nesting == 0) {
					if (previousChar == '/') {
						isFlat = true;
						sb.deleteCharAt(sb.length() - 1);
					}
					// check for new line
					if (reader.hasRemaining()) {
						int readAmt = 0;
						if (reader.charAt(0) == '\r')
							readAmt++;
						if (reader.charAt(readAmt) == '\n') {
							readAmt++;
							for (int i = 0; i < readAmt; i++)
								reader.get();
						}
					}
					if (startChars.length == 2) {
						return new TemplateDefinedParameter(sb.toString(),
								isFlat, session, startPos);
					} else {
						return new TemplateDefinedParameter(sb.toString(),
								isFlat, true, session, startPos);
					}
				}
			} else if (c == '\\') {
				// escape sequence
				if (reader.hasRemaining()) {
					sb.append(reader.get());
					continue;
				}
			}
			previousChar = c;
			sb.append(c);
		}
		return null;
	}
}