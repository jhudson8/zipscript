/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.util;

import hudson.zipscript.parser.template.data.ElementIndex;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.special.SpecialElement;
import hudson.zipscript.parser.template.element.special.SpecialStringElement;
import hudson.zipscript.parser.template.element.special.SpecialStringElementImpl;

import java.util.List;

public class SpecialElementNormalizer {

	public static ElementIndex normalizeSpecialElement(SpecialElement element,
			int index, List elementList, ParsingSession session) {
		StringBuffer text = new StringBuffer();
		text.append(element.getTokenValue());
		boolean appended = false;
		while (elementList.size() > index) {
			Element e = (Element) elementList.get(index);
			if (e instanceof SpecialStringElement) {
				elementList.remove(index);
				text.append(((SpecialStringElement) e).getTokenValue());
				appended = true;
			} else if (e instanceof SpecialElement) {
				elementList.remove(index);
				text.append(((SpecialElement) e).getTokenValue());
				appended = true;
			} else {
				break;
			}
		}
		if (appended) {
			return new ElementIndex(new SpecialStringElementImpl(text
					.toString()), index);
		} else
			return null;
	}
}
