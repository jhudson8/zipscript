/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.comment;

import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.AbstractPatternMatcher;
import hudson.zipscript.parser.template.element.Element;

import java.nio.CharBuffer;
import java.util.List;

public class CommentPatternMatcher extends AbstractPatternMatcher {

	public Element match(char previousChar, char[] startChars,
			CharBuffer contents, ParsingSession parseData, List elements,
			StringBuffer unmatchedChars) throws ParseException {
		Element e = super.match(previousChar, startChars, contents, parseData,
				elements, unmatchedChars);
		if (null != e && contents.hasRemaining()) {
			if (contents.charAt(0) == '\r')
				contents.get();
			if (contents.charAt(0) == '\n')
				contents.get();
		}
		return e;
	}

	protected Element createElement(char[] startToken, String s,
			int contentStartPosition, ParsingSession parseData)
			throws ParseException {
		return CommentElement.getInstance();
	}

	protected char[] getEndChars() {
		return "##]".toCharArray();
	}

	public char[] getStartToken() {
		return "[##".toCharArray();
	}

	public char[][] getStartTokens() {
		return null;
	}

	protected boolean isNestingApplicable() {
		return false;
	}
}