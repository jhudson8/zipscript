/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable;

import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.PatternMatcher;
import hudson.zipscript.parser.util.StringUtil;

import java.nio.CharBuffer;
import java.util.List;

public class VariablePatternMatcher implements PatternMatcher {

	public char[] getStartToken() {
		return "$".toCharArray();
	}

	public char[][] getStartTokens() {
		return null;
	}

	public Element match(char previousChar, char[] startChars,
			CharBuffer reader, ParsingSession session, List elements,
			StringBuffer unmatchedChars) throws ParseException {
		if (StringUtil.isEscaped(unmatchedChars))
			return null;
		boolean isFormal = false;
		boolean isSilenced = false;
		if (reader.hasRemaining()) {
			char c = reader.get();
			if (c == '!')
				isSilenced = true;
			else if (c == '{')
				isFormal = true;
			else
				reader.position(reader.position() - 1);
		}
		if (isSilenced) {
			// might be formal as well
			char c = reader.get();
			if (c == '{')
				isFormal = true;
			else
				reader.position(reader.position() - 1);
		}
		int startPosition = reader.position();

		StringBuffer sb = new StringBuffer();
		int squiglyNesting = 0;
		if (isFormal)
			squiglyNesting = 1;
		int parenNesting = 0;
		int braceNesting = 0;
		boolean inString = false;
		char stringChar = Character.MIN_VALUE;
		while (reader.hasRemaining()) {
			char c = reader.get();
			if (!inString) {
				if (c == '{') {
					squiglyNesting++;
				} else if (c == '}') {
					squiglyNesting--;
					if (squiglyNesting == 0 && parenNesting == 0
							&& braceNesting == 0) {
						// numbers must be referenced formally
						try {
							Float.parseFloat(sb.toString());
							return null;
						} catch (Exception e) {
							return new VariableElement(isFormal, isSilenced, sb
									.toString(), session, startPosition);
						}
					}

				} else if (c == '$') {
					// we can't process this
					return null;
				} else if (c == '(') {
					parenNesting++;
				} else if (c == ')') {
					parenNesting--;
				} else if (c == '[') {
					braceNesting++;
				} else if (c == ']') {
					braceNesting--;
				} else if (c == '\'' || c == '\"') {
					// we can't have strings in lazy variables if we're not
					// nested
					if (!isFormal && squiglyNesting == 0 && parenNesting == 0
							&& braceNesting == 0) {
						return null;
					}
					inString = true;
					stringChar = c;
				} else if (!isFormal) {
					if (Character.isWhitespace(c)) {
						if (squiglyNesting == 0 && parenNesting == 0
								&& braceNesting == 0) {
							reader.position(reader.position() - 1);
							return new VariableElement(isFormal, isSilenced, sb
									.toString(), session, startPosition);
						}
					} else if (!(Character.isLetterOrDigit(c) || c == '_'
							|| c == '.' || c == ',')) {
						return null;
					}
				}
				sb.append(c);
			} else {
				if (c == '\\') {
					if (reader.hasRemaining())
						sb.append(reader.get());
					else
						return null;
				} else if (c == stringChar) {
					inString = false;
					stringChar = Character.MIN_VALUE;
					sb.append(c);
				} else {
					sb.append(c);
				}
			}
		}
		if (!isFormal) {
			if (squiglyNesting == 0 && parenNesting == 0 && braceNesting == 0) {
				return new VariableElement(isFormal, isSilenced, sb.toString(),
						session, startPosition);
			}
		}
		return null;
	}
}