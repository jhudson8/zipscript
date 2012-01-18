/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.special;

import hudson.zipscript.parser.template.element.Element;

public class InPatternMatcher extends WordPatternMatcher {

	protected String getWord() {
		return "in";
	}

	public Element getElement() {
		return InElement.getInstance();
	}
}