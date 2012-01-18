/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.comparator;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ElementIndex;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.AbstractElement;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.lang.WhitespaceElement;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractComparatorElement extends AbstractElement
		implements ComparatorElement {

	private Element leftHandSide;
	private Element rightHandSide;

	public boolean booleanValue(ExtendedContext context)
			throws ExecutionException {
		Object lhsValue = getValue(leftHandSide, context);
		Object rhsValue = getValue(rightHandSide, context);
		return compare(lhsValue, rhsValue);
	}

	public Object objectValue(ExtendedContext context)
			throws ExecutionException {
		return new Boolean(booleanValue(context));
	}

	protected Object getValue(Element element, ExtendedContext context)
			throws ExecutionException {
		return element.objectValue(context);
	}

	protected abstract boolean compare(Object lhs, Object rhs);

	protected abstract String getComparatorString();

	public void merge(ExtendedContext context, Writer sw)
			throws ExecutionException {
		Object rtn = objectValue(context);
		if (null != rtn) {
			try {
				sw.write(rtn.toString().toCharArray());
			} catch (IOException e) {
			}
		}
	}

	public ElementIndex normalize(int index, List elements,
			ParsingSession session) throws ParseException {
		if (elements.size() == 1) {
			return null;
		}
		int returnIndex = index;
		int checkIndex = index;

		// set right hand side and left hand side
		Element rhs = null;
		while (null == rhs || rhs instanceof WhitespaceElement) {
			rhs = (Element) elements.remove(index);
		}
		ElementIndex ei = rhs.normalize(index, elements, session);
		if (null != ei)
			rhs = ei.getElement();
		setRightHandSide(rhs);

		Element lhs = null;
		int i = 1;
		while (null == lhs || lhs instanceof WhitespaceElement) {
			if (index > 0) {
				lhs = (Element) elements.remove(index - i);
				i++;
				returnIndex--;
				checkIndex--;
			} else {
				throw new ParseException(this, "Improperly formed expression '"
						+ this.toString() + "'");
			}
		}
		setLeftHandSide(lhs);

		// see if we need to deal with priority
		for (int j = checkIndex; j < elements.size(); j++) {
			if (elements.get(j) instanceof WhitespaceElement)
				continue;
			else if (elements.get(j) instanceof ComparatorElement) {
				ComparatorElement ce = (ComparatorElement) elements.get(j);
				if (ce.getPriority() < this.getPriority()) {
					// this will be our new right side
					// reset the right side context
					elements.add(j, getRightHandSide());
					setRightHandSide((ComparatorElement) elements.remove(j + 1));
					ce.normalize(j + 1, elements, session);
					break;
				}
			} else
				break;
		}
		return new ElementIndex(this, returnIndex);
	}

	/**
	 * @return the leftHandSide
	 */
	public Element getLeftHandSide() {
		return leftHandSide;
	}

	/**
	 * @param leftHandSide
	 *            the leftHandSide to set
	 */
	public void setLeftHandSide(Element leftHandSide) {
		this.leftHandSide = leftHandSide;
	}

	/**
	 * @return the rightHandSide
	 */
	public Element getRightHandSide() {
		return rightHandSide;
	}

	/**
	 * @param rightHandSide
	 *            the rightHandSide to set
	 */
	public void setRightHandSide(Element rightHandSide) {
		this.rightHandSide = rightHandSide;
	}

	List children = null;

	public List getChildren() {
		if (null == children) {
			children = new ArrayList();
			children.add(getLeftHandSide());
			children.add(getRightHandSide());
		}
		return children;
	}

	public String toString() {
		return getLeftHandSide() + " " + getComparatorString() + " "
				+ getRightHandSide();
	}
}