/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.special;

import hudson.zipscript.parser.template.element.Element;

public class WithPatternMatcher extends WordPatternMatcher {

	protected String getWord() {
		return "with";
	}

	public Element getElement() {
		return WithElement.getInstance();
	}
}