/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable.special.string;

public class XMLSpecialMethod extends AbstractReplacementMethod {

	public static final XMLSpecialMethod INSTANCE = new XMLSpecialMethod();

	protected char[] getCharsToReplace() {
		return new char[] { '<', '>', '&', '\"', '&' };
	}

	protected String[] getReplacementStrings() {
		return new String[] { "&lt;", "&gt;", "&amp;", "&quot;", "&apos;" };
	}
}