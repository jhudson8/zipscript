/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.group;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ElementIndex;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.NestableElement;
import hudson.zipscript.parser.template.element.lang.variable.VariableElement;
import hudson.zipscript.parser.util.StringUtil;

import java.io.Writer;
import java.util.List;

public class MapElement extends NestableElement {

	protected boolean isStartElement(
			hudson.zipscript.parser.template.element.Element e) {
		return (e instanceof MapElement);
	}

	protected boolean isEndElement(Element e) {
		return (e instanceof EndMapElement);
	}

	public void merge(ExtendedContext context, Writer sw) {
		StringUtil.append('[', sw);
	}

	public boolean booleanValue(ExtendedContext context)
			throws ExecutionException {
		if (getChildren().size() == 1)
			return ((Element) getChildren().get(0)).booleanValue(context);
		else
			throw new ExecutionException(
					"groups can not be evaluated as booleans", this);
	}

	public Object objectValue(ExtendedContext context)
			throws ExecutionException {
		if (getChildren().size() == 1)
			return ((Element) getChildren().get(0)).objectValue(context);
		else
			throw new ExecutionException(
					"groups can not be evaluated as objects", this);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		if (null != getChildren()) {
			for (int i = 0; i < getChildren().size(); i++) {
				if (i > 0)
					sb.append(' ');
				sb.append(getChildren().get(i));
			}
		}
		sb.append("]");
		return sb.toString();
	}
}
