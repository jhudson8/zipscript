/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.directive.macrodir;

import hudson.zipscript.parser.ExpressionParser;
import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ElementIndex;
import hudson.zipscript.parser.template.data.ParsingResult;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.AbstractElement;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.ToStringWithContextElement;
import hudson.zipscript.parser.template.element.lang.TextDefaultElementFactory;
import hudson.zipscript.parser.util.StringUtil;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

public class MacroHeaderElement extends AbstractElement implements
		ToStringWithContextElement {

	private List children;
	private String contents;
	private int position;

	public MacroHeaderElement(String contents, ParsingSession session,
			int position) throws ParseException {
		this.contents = contents;
		this.position = position;
	}

	public List getChildren() {
		return children;
	}

	public void merge(ExtendedContext context, Writer sw)
			throws ExecutionException {
		if (null != children) {
			for (Iterator i = children.iterator(); i.hasNext();) {
				((Element) i.next()).merge(context, sw);
			}
		}
	}

	public ElementIndex normalize(int index, List elementList,
			ParsingSession session) throws ParseException {
		// set element in macro
		((MacroInstanceDirective) session.getNestingStack().get(
				session.getNestingStack().size() - 1)).setHeader(this);
		return new ElementIndex(null, -1);
	}

	public void validate(ParsingSession session) throws ParseException {
		if (null != contents) {
			boolean hideEscaping = session.isHideEscapeMethods();
			session.setHideEscapeMethods(true);
			ParsingResult result = ExpressionParser.getInstance().parse(
					contents, session.getResourceContainer().getComponents(),
					TextDefaultElementFactory.INSTANCE, position, session);
			children = result.getElements();
			StringUtil.trim(children);
			this.contents = null;
			session.setHideEscapeMethods(hideEscaping);
		}
	}

	public Object objectValue(ExtendedContext context)
			throws ExecutionException {
		return null;
	}

	public boolean booleanValue(ExtendedContext context)
			throws ExecutionException {
		return false;
	}

	public String toString(ExtendedContext context) {
		StringWriter sw = new StringWriter();
		append(context, sw);
		return sw.toString();
	}

	public void append(ExtendedContext context, Writer writer) {
		merge(context, writer);
	}
}