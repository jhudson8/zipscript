/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.directive;

public abstract class AbstractEndPatternMatcher extends
		AbstractDirectivePatternMatcher {

	private char[] startToken;

	public char[] getStartToken() {
		if (null == startToken) {
			startToken = new char[3 + getDirectiveName().length()];
			startToken[0] = '[';
			startToken[1] = '/';
			startToken[2] = '#';
			System.arraycopy(getDirectiveName().toCharArray(), 0, startToken,
					3, getDirectiveName().length());
		}
		return startToken;
	}

	protected boolean onlyAllowEmpty() {
		return true;
	}
}
