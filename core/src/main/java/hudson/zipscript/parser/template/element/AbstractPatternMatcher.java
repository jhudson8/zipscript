/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element;

import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.util.StringUtil;

import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractPatternMatcher implements PatternMatcher {

	protected String read(CharBuffer contents, int position, int length,
			int endCharLength) {
		contents.position(position);
		char[] arr = new char[length];
		contents.get(arr);
		String s = new String(arr);
		s = s.substring(0, s.length() - endCharLength);
		return s;
	}

	private static final Map nestingMap = new HashMap();
	static {
		nestingMap.put(new Character('['), new Character(']'));
		nestingMap.put(new Character('('), new Character(')'));
		nestingMap.put(new Character('{'), new Character('}'));
	}

	protected int findMatch(CharBuffer contents, char[] startChars,
			char[] endChars, char[] stopChars, boolean allowEscape)
			throws ParseException {
		int position = contents.position() - startChars.length;
		int nesting = 1;
		if (!isNestingApplicable())
			nesting = 0;
		char startMatchStart = startChars[0];
		char endMatchStart = endChars[0];
		int length = 0;
		while (true) {
			if (!contents.hasRemaining()) {
				throw new ParseException(position,
						"Unexpected end of file reached while looking for '"
								+ new String(endChars) + "'");
			}
			char c = contents.get();
			if (nesting > 1) {
				// forget about end match for now because we have an additional
				// nesting
				Character nestingEndMatch = (Character) nestingMap
						.get(new Character(startMatchStart));
				if (null != nestingEndMatch && nestingEndMatch.charValue() == c) {
					nesting--;
					length++;
					continue;
				}
			}
			if (c == endMatchStart) {
				// possible match
				if (isNestingApplicable())
					nesting--;
				if (nesting > 0 && isNestingApplicable()) {
					length++;
					continue;
				}
				if (contents.length() >= endChars.length - 1) {
					boolean match = true;
					for (int i = 1; i < endChars.length; i++) {
						if (contents.charAt(i - 1) != endChars[i])
							match = false;
						break;
					}
					if (match) {
						if (length == 0 && !allowEmpty() && !onlyAllowEmpty()) {
							char[] arr = new char[length];
							contents.get(arr);
							throw new ParseException(position, length
									+ contents.length(),
									"Missing content for '"
											+ new String(startChars)
											+ new String(getEndChars()) + "'");
						}
						if (length > 0 && onlyAllowEmpty()) {
							char[] arr = new char[length];
							contents.get(arr);
							throw new ParseException(position, length
									+ contents.length(),
									"No content is allowed inside '"
											+ new String(startChars)
											+ new String(getEndChars()) + "'");
						}
						length += endChars.length;
						return length;
					}
				} else {
					// we really shouldn't get here
					throw new ParseException(position, length
							+ contents.length(),
							"Undetermined processing error");
				}
			} else if (c == startMatchStart) {
				if (isNestingApplicable())
					nesting++;
			} else if (isMatch(c, stopChars)) {
				throw new ParseException(position, length,
						"Invalid character '" + c + "'");
			}
			length++;
		}
	}

	public Element match(char previousChar, char[] startChars,
			CharBuffer contents, ParsingSession parseData, List elements,
			StringBuffer unmatchedChars) throws ParseException {
		if (StringUtil.isEscaped(unmatchedChars))
			return null;
		int position = contents.position();
		char[] endChars = getEndChars();
		int length = findMatch(contents, startChars, endChars,
				getInvalidChars(), true);
		String s = read(contents, position, length, endChars.length);
		Element rtn = createElement(startChars, s, position, parseData);
		rtn = onCreateElement(rtn, startChars, s, position, contents);
		return rtn;
	}

	protected Element onCreateElement(Element element, char[] startChars,
			String contents, int contentStartPosition, CharBuffer buffer) {
		return element;
	}

	protected boolean isMatch(char c, char[] arr) {
		if (null == arr)
			return false;
		for (int i = 0; i < arr.length; i++) {
			if (c == arr[i])
				return true;
		}
		return false;
	}

	protected boolean allowEmpty() {
		return false;
	}

	protected boolean onlyAllowEmpty() {
		return false;
	}

	protected boolean isNestingApplicable() {
		return true;
	}

	protected abstract char[] getEndChars();

	protected char[] getInvalidChars() {
		return null;
	}

	protected abstract Element createElement(char[] startToken, String s,
			int contentStartPosition, ParsingSession parseData)
			throws ParseException;
}