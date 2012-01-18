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

public class EscapeDirective extends NestableElement implements
		MacroInstanceAware {

	private SpecialMethod escapeMethod;

	public EscapeDirective(String contents, int contentIndex,
			ParsingSession parsingSession) throws ParseException {
		String method = contents.trim();
		escapeMethod = parsingSession.getResourceContainer()
				.getVariableAdapterFactory().getStringEscapingStringMethod(
						method, parsingSession);
		if (null == escapeMethod)
			throw new ParseException(contentIndex, "Undefined escape method '"
					+ escapeMethod + "'");
	}

	public ElementIndex normalize(int index, List elementList,
			ParsingSession session) throws ParseException {
		session.addEscapeMethod(escapeMethod);
		ElementIndex rtn = super.normalize(index, elementList, session);
		session.removeEscapeMethod(escapeMethod);
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
		return (e instanceof EscapeDirective);
	}

	protected boolean isEndElement(Element e) {
		return (e instanceof EndEscapeDirective);
	}

	public String toString() {
		return "[#escape " + escapeMethod + "]";
	}
}