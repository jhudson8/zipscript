/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.directive.macrodir;

import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.PatternMatcher;

import java.nio.CharBuffer;
import java.util.List;

public class EndMacroInstancePatternMatcher implements PatternMatcher {

	boolean isFlat = false;

	public char[] getStartToken() {
		return null;
	}

	public char[][] getStartTokens() {
		return new char[][] { "[/.@".toCharArray(), "[/@".toCharArray() };
	}

	public Element match(char previousChar, char[] startChars,
			CharBuffer reader, ParsingSession session, List elements,
			StringBuffer unmatchedChars) throws ParseException {
		StringBuffer sb = new StringBuffer();
		while (reader.hasRemaining()) {
			char c = reader.get();
			if (c == '[') {
				throw new ParseException(reader.position(),
						"Unexpected content '['");
			} else if (c == ']') {
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
				if (startChars.length == 3)
					return new EndMacroInstanceDirective(sb.toString().trim());
				else
					return new EndMacroInstanceDirective(sb.toString().trim(),
							true);
			}
			sb.append(c);
		}
		return null;
	}
}