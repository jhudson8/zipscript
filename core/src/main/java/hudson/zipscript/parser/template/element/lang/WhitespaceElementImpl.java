/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang;

import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ElementIndex;
import hudson.zipscript.parser.template.data.ParseParameters;

import java.util.List;

public class WhitespaceElementImpl extends IdentifierElement implements
		WhitespaceElement {

	public ElementIndex normalize(int index, List elementList,
			ParseParameters parameters) throws ParseException {
		while (index < elementList.size()) {
			if (elementList.get(index) instanceof WhitespaceElement) {
				elementList.remove(index);
			} else {
				break;
			}
		}
		return null;
	}

	public String toString() {
		return " ";
	}
}
