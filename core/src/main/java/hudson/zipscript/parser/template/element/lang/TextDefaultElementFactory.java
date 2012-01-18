/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang;

import hudson.zipscript.ext.data.DefaultElementContainer;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.DefaultElementFactory;
import hudson.zipscript.parser.template.element.Element;

public class TextDefaultElementFactory implements DefaultElementFactory {

	public static TextDefaultElementFactory INSTANCE = new TextDefaultElementFactory();

	public DefaultElementContainer createDefaultElement(
			Element nextElement, String text, ParsingSession session,
			int currentPosition) {
		return new DefaultElementContainer(
			new TextElement(text), nextElement);
	}

	public boolean doAppend(char c) {
		return true;
	}
}
