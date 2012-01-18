/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ElementIndex;
import hudson.zipscript.parser.template.data.ParsingSession;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

public class CompositeElement implements Element {

	public Element referenceElement;
	public List children;

	public CompositeElement(List children, Element referenceElement) {
		this.children = children;
		this.referenceElement = referenceElement;
	}

	public List getChildren() {
		return children;
	}

	public int getElementLength() {
		return referenceElement.getElementLength();
	}

	public long getElementPosition() {
		return referenceElement.getElementPosition();
	}

	public void merge(ExtendedContext context, Writer sw)
			throws ExecutionException {
		for (Iterator i = children.iterator(); i.hasNext();) {
			((Element) i.next()).merge(context, sw);
		}
	}

	public ElementIndex normalize(int index, List elementList,
			ParsingSession session) throws ParseException {
		return null;
	}

	public Object objectValue(ExtendedContext context)
			throws ExecutionException {
		StringWriter sw = new StringWriter();
		merge(context, sw);
		return sw.toString();
	}

	public boolean booleanValue(ExtendedContext context)
			throws ExecutionException {
		throw new ExecutionException("Invalid boolean element '"
				+ referenceElement + "'", referenceElement);
	}

	public void setElementLength(int length) {
		referenceElement.setElementLength(length);
	}

	public void setElementPosition(long position) {
		referenceElement.setElementPosition(position);
	}

	public void validate(ParsingSession session) throws ParseException {
	}
}