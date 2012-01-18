/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.special;

import hudson.zipscript.ext.data.DefaultElementContainer;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.DefaultElementFactory;
import hudson.zipscript.parser.template.element.Element;

public class SpecialStringDefaultEelementFactory implements
		DefaultElementFactory {

	private static final SpecialStringDefaultEelementFactory INSTANCE = new SpecialStringDefaultEelementFactory();

	public static final SpecialStringDefaultEelementFactory getInstance() {
		return INSTANCE;
	}

	public DefaultElementContainer createDefaultElement(Element nextElement, String text, ParsingSession session,
			int contentPosition) {
		return new DefaultElementContainer(
				new SpecialStringElementImpl(text), nextElement);
	}

	public boolean doAppend(char c) {
		return true;
	}
}
