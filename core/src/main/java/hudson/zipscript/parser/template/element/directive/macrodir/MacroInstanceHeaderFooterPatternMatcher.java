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

public class MacroInstanceHeaderFooterPatternMatcher implements PatternMatcher {

	public char[] getStartToken() {
		return "[[".toCharArray();
	}

	public char[][] getStartTokens() {
		return null;
	}

	public Element match(char previousChar, char[] startChars,
			CharBuffer reader, ParsingSession session, List elements,
			StringBuffer unmatchedChars) throws ParseException {
		if (elements.size() > 0) {
			// header element
			int nesting = 1;
			previousChar = Character.MIN_VALUE;
			StringBuffer sb = new StringBuffer();
			int startPosition = reader.position();
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
						if (reader.hasRemaining()) {
							if (reader.charAt(0) == ']') {
								// is this a header or footer?
								if (elements.get(elements.size() - 1) instanceof MacroInstanceDirective
										&& unmatchedChars.toString().trim()
												.length() == 0) {
									unmatchedChars.delete(0, unmatchedChars
											.length());
									reader.get(); // remove last ']'
									StringUtil.trimFirstEmptyLine(reader);
									return new MacroHeaderElement(
											sb.toString(), session,
											startPosition);
								} else {
									// make sure this is a valid footer - must
									// be right before end directive
									reader.get();
									while (reader.hasRemaining()) {
										c = reader.get();
										if (c == '[') {
											// beginning of ending element?
											if (reader.length() > 2) {
												if (reader.charAt(0) == '/'
														&& reader.charAt(1) == '@') {
													reader.position(reader
															.position() - 1);
													StringUtil
															.trimLastEmptyLine(unmatchedChars);
													return new MacroFooterElement(
															sb.toString(),
															session,
															startPosition);
												}
											}
										} else if (!Character.isWhitespace(c)) {
											break;
										}
									}
									return null;
								}
							}
						} else
							return null;
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
		}
		return null;
	}
}