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

public class InElement extends IdentifierElement implements SpecialElement {

	private static final InElement instance = new InElement();

	public static final InElement getInstance() {
		return instance;
	}

	private InElement() {
	}

	public String getTokenValue() {
		return "in";
	}

	public ElementIndex normalize(int index, List elementList,
			ParsingSession session) throws ParseException {
		return SpecialElementNormalizer.normalizeSpecialElement(this, index,
				elementList, session);
	}

}
