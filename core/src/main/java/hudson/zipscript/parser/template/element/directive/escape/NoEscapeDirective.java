/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.directive.escape;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ElementIndex;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.NestableElement;
import hudson.zipscript.parser.template.element.directive.macrodir.MacroDirective;
import hudson.zipscript.parser.template.element.directive.macrodir.MacroInstanceAware;
import hudson.zipscript.parser.template.element.lang.variable.special.SpecialMethod;

import java.io.Writer;
import java.util.List;
import java.util.Map;

public class NoEscapeDirective extends NestableElement implements
		MacroInstanceAware {

	public NoEscapeDirective(String contents, int contentIndex,
			ParsingSession parsingSession) throws ParseException {
	}

	public ElementIndex normalize(int index, List elementList,
			ParsingSession session) throws ParseException {
		SpecialMethod escapeMethod = session.removeEscapeMethod(null);
		ElementIndex rtn = super.normalize(index, elementList, session);
		session.addEscapeMethod(escapeMethod);
		return rtn;
	}

	public void merge(ExtendedContext context, Writer sw)
			throws ExecutionException {
		appendElements(getChildren(), context, sw);
	}

	public void getMatchingTemplateDefinedParameters(ExtendedContext context,
			List macroInstanceList, MacroDirective macro,
			Map additionalContextEntries) throws ExecutionException {
		appendTemplateDefinedParameters(getChildren(), context,
				macroInstanceList, macro, additionalContextEntries);
	}

	protected boolean isStartElement(Element e) {
		return (e instanceof NoEscapeDirective);
	}

	protected boolean isEndElement(Element e) {
		return (e instanceof EndNoEscapeDirective);
	}

	public String toString() {
		return "[#noescape]";
	}
}