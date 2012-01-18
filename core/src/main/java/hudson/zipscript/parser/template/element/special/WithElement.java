/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.special;

import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ElementIndex;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.lang.IdentifierElement;
import hudson.zipscript.parser.util.SpecialElementNormalizer;

import java.util.List;

public class WithElement extends IdentifierElement implements SpecialElement {

	private static final WithElement instance = new WithElement();

	public static final WithElement getInstance() {
		return instance;
	}

	private WithElement() {
	}

	public String getTokenValue() {
		return "with";
	}

	public ElementIndex normalize(int index, List elementList,
			ParsingSession session) throws ParseException {
		return SpecialElementNormalizer.normalizeSpecialElement(this, index,
				elementList, session);
	}
}
