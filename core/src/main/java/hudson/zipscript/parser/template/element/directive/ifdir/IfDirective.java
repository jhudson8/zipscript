/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.directive.ifdir;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.HeaderElementList;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.DebugElementContainerElement;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.NestableElement;
import hudson.zipscript.parser.template.element.directive.macrodir.MacroDirective;
import hudson.zipscript.parser.template.element.directive.macrodir.MacroInstanceAware;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class IfDirective extends NestableElement implements MacroInstanceAware,
		DebugElementContainerElement {

	public Element ifElement;
	public List ifElements;
	private List elseifScenarios;
	private List elseElements;

	public IfDirective(String contents, int contentIindex,
			ParsingSession parsingSession) throws ParseException {
		setParsingSession(parsingSession);
		parseContents(contents, contentIindex, parsingSession);
	}

	public List getInternalElements() {
		List list = new ArrayList();
		list.add(ifElement);
		if (null != elseifScenarios) {
			for (Iterator i = elseifScenarios.iterator(); i.hasNext();) {
				HeaderElementList hel = (HeaderElementList) i.next();
				list.add(hel.getHeader());
			}
		}
		return list;
	}

	private void parseContents(String contents, int contentIindex,
			ParsingSession parsingSession) throws ParseException {
		ifElement = parseElement(contents, parsingSession, contentIindex);
		if (null == ifElement)
			throw new ParseException(this, "Invalid element syntax '"
					+ contents + "'");
	}

	protected boolean isTopLevelElement(Element e) {
		return (e instanceof ElseIfDirective || e instanceof ElseDirective);
	}

	protected void setTopLevelElements(HeaderElementList elements,
			ParsingSession parsingSession) throws ParseException {
		if (elements.getHeader() instanceof ElseIfDirective) {
			if (null == elseifScenarios)
				elseifScenarios = new ArrayList();
			ElseIfDirective directive = (ElseIfDirective) elements.getHeader();
			Element element = parseElement(directive.getContents(),
					parsingSession, (int) elements.getHeader()
							.getElementPosition());
			elements.setHeader(element);
			elseifScenarios.add(elements);
		} else if (elements.getHeader() instanceof ElseDirective) {
			elseElements = elements.getChildren();
		}
	}

	public void setChildren(List children) {
		this.ifElements = children;
	}

	public List getChildren() {
		List children = new ArrayList();
		if (null != ifElements)
			children.addAll(ifElements);
		if (null != elseifScenarios) {
			for (Iterator i = elseifScenarios.iterator(); i.hasNext();) {
				HeaderElementList hel = (HeaderElementList) i.next();
				children.addAll(hel.getChildren());
			}
		}
		if (null != elseElements)
			children.addAll(elseElements);
		return children;
	}

	public void merge(ExtendedContext context, Writer sw)
			throws ExecutionException {
		boolean done = false;
		boolean isDebug = getParsingSession().isDebug();
		if (isDebug)
			System.out.println("Executing: If: " + ifElement);
		if (ifElement.booleanValue(context)) {
			if (isDebug)
				System.out.println("Executing: If: " + ifElement
						+ "; Evaluation Successful");
			appendElements(ifElements, context, sw);
			done = true;
		}
		if (!done && null != elseifScenarios) {
			for (int i = 0; i < elseifScenarios.size() && !done; i++) {
				HeaderElementList elements = (HeaderElementList) elseifScenarios
						.get(i);
				if (isDebug)
					System.out.println("Executing: ElseIf: "
							+ elements.getHeader());
				if (elements.getHeader().booleanValue(context)) {
					if (isDebug)
						System.out.println("Executing: ElseIf: "
								+ elements.getHeader()
								+ "; Evaluation Successful");
					appendElements(elements.getChildren(), context, sw);
					done = true;
					break;
				}
			}
		}
		if (!done && null != elseElements) {
			if (isDebug)
				System.out.println("Executing: Else");
			appendElements(elseElements, context, sw);
		}
	}

	public void getMatchingTemplateDefinedParameters(ExtendedContext context,
			List macroInstanceList, MacroDirective macro,
			Map additionalContextEntries) throws ExecutionException {
		boolean done = false;
		if (ifElement.booleanValue(context)) {
			appendTemplateDefinedParameters(getChildren(), context,
					macroInstanceList, macro, additionalContextEntries);
			done = true;
		}
		if (null != elseifScenarios) {
			for (int i = 0; i < elseifScenarios.size() && !done; i++) {
				HeaderElementList elements = (HeaderElementList) elseifScenarios
						.get(i);
				if (elements.getHeader().booleanValue(context)) {
					appendTemplateDefinedParameters(elements.getChildren(),
							context, macroInstanceList, macro,
							additionalContextEntries);
					done = true;
				}
			}
		}
		if (!done && null != elseElements) {
			appendTemplateDefinedParameters(elseElements, context,
					macroInstanceList, macro, additionalContextEntries);
		}
	}

	protected boolean isStartElement(Element e) {
		return (e instanceof IfDirective);
	}

	protected boolean isEndElement(Element e) {
		return (e instanceof EndIfDirective);
	}

	/**
	 * @return the elseifScenarios
	 */
	public List getElseifScenarios() {
		return elseifScenarios;
	}

	/**
	 * @return the elseElements
	 */
	public List getElseElements() {
		return elseElements;
	}

	public String toString() {
		return "[#if " + ifElement + "]";
	}
}