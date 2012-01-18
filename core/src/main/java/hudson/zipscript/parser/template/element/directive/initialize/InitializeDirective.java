/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.directive.initialize;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.NestableElement;
import hudson.zipscript.parser.template.element.NonOutputElement;

import java.io.Writer;
import java.util.Iterator;

public class InitializeDirective extends NestableElement implements
		NonOutputElement {

	private static final NullWriter NULL_WRITER = new NullWriter();

	public void merge(ExtendedContext context, Writer sw)
			throws ExecutionException {
		// this has no output
	}

	protected boolean isStartElement(Element e) {
		return (e instanceof InitializeDirective);
	}

	protected boolean isEndElement(Element e) {
		return (e instanceof EndInitializeDirective);
	}

	public String toString() {
		return "[#initialize]";
	}

	public boolean generatesOutput() {
		return false;
	}

	public void doInitialize(ExtendedContext context) {
		for (Iterator i = getChildren().iterator(); i.hasNext();) {
			((Element) i.next()).merge(context, NULL_WRITER);
		}
	}
}