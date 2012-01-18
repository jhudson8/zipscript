/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.comparator.logic;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.comparator.AbstractComparatorElement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OrLogicElement extends AbstractComparatorElement {

	private List elements;

	public boolean booleanValue(ExtendedContext context)
			throws ExecutionException {
		if (null == elements) {
			elements = new ArrayList();
			elements.add(getLeftHandSide());
			elements.add(getRightHandSide());
		}
		for (Iterator i = elements.iterator(); i.hasNext();) {
			Element e = (Element) i.next();
			if (e.booleanValue(context))
				return true;
		}
		return false;
	}

	protected boolean compare(Object lhs, Object rhs) {
		// not used
		return false;
	}

	public int getPriority() {
		return 51;
	}

	public String getComparatorString() {
		return "||";
	}
}
