/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element;

import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ParsingSession;

import java.nio.CharBuffer;
import java.util.List;

/**
 * Used for matching a expression resource pattern
 * 
 * @author Joe Hudson
 */

public interface PatternMatcher {

	/**
	 * Return the start token or null if getStartTokens() is used
	 */
	public char[] getStartToken();

	/**
	 * Return the array of start tokens or null if getStartToken() is used
	 */
	public char[][] getStartTokens();

	/**
	 * Called if the document matched this pattern mather's start token
	 * 
	 * @param previousChar
	 *            the previously read character (or Character.MIN_VALUE if first
	 *            char)
	 * @param startChars
	 *            the start characters which executed this pattern matcher
	 * @param reader
	 *            the template contents
	 * @param session
	 *            the parsing session
	 * @param elements
	 *            previously matched elements
	 * @param unmatched
	 *            any characters after the last element in the list that do not
	 *            belong to an element
	 * @return an element match or null if no match
	 * @throws ParseException
	 */
	public Element match(char previousChar, char[] startChars,
			CharBuffer reader, ParsingSession session, List elements,
			StringBuffer unmatchedChars) throws ParseException;
}
