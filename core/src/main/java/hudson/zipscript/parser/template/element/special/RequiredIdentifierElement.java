/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.special;

import hudson.zipscript.parser.template.element.lang.IdentifierElement;

public class RequiredIdentifierElement extends IdentifierElement implements
		SpecialElement {

	private static final RequiredIdentifierElement instance = new RequiredIdentifierElement();

	public static final RequiredIdentifierElement getInstance() {
		return instance;
	}

	private RequiredIdentifierElement() {
	}

	public String getTokenValue() {
		return "*";
	}

}
