/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.special;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ElementIndex;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.AbstractElement;
import hudson.zipscript.parser.util.SpecialElementNormalizer;

import java.io.Writer;
import java.util.List;

public class NullElement extends AbstractElement implements SpecialElement {

	private static final NullElement instance = new NullElement();

	public static final NullElement getInstance() {
		return instance;
	}

	private NullElement() {
	}

	public String getTokenValue() {
		return "null";
	}

	public void merge(ExtendedContext context, Writer sw) {
	}

	public boolean booleanValue(ExtendedContext context) {
		return false;
	}

	public Object objectValue(ExtendedContext context) {
		return null;
	}

	public ElementIndex normalize(int index, List elementList,
			ParsingSession session) throws ParseException {
		return SpecialElementNormalizer.normalizeSpecialElement(this, index,
				elementList, session);
	}

	public List getChildren() {
		return null;
	}
}
