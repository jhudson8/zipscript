/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable.special.string;

public class JSSpecialMethod extends AbstractReplacementMethod {

	public static final JSSpecialMethod INSTANCE = new JSSpecialMethod();

	protected char[] getCharsToReplace() {
		return new char[] { '\"', '\'', '>', '\r', '\n' };
	}

	protected String[] getReplacementStrings() {
		return new String[] { "\\\"", "\\'", "\\>", "\\r", "\\n" };
	}
}