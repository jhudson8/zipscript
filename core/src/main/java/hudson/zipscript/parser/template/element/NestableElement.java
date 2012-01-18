/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ElementIndex;
import hudson.zipscript.parser.template.data.HeaderElementList;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.directive.AbstractDirective;
import hudson.zipscript.parser.template.element.directive.macrodir.MacroDirective;
import hudson.zipscript.parser.template.element.directive.macrodir.MacroInstanceAware;
import hudson.zipscript.parser.template.element.directive.macrodir.MacroInstanceDirective;
import hudson.zipscript.parser.template.element.directive.macrodir.MacroInstanceEntity;
import hudson.zipscript.parser.template.element.directive.macrodir.TemplateDefinedParameter;
import hudson.zipscript.parser.util.ElementNormalizer;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class NestableElement extends AbstractDirective {

	private List children;
	private boolean isFlat;

	/**
	 * @return the children
	 */
	public List getChildren() {
		return children;
	}

	/**
	 * @param children
	 *            the children to set
	 */
	public void setChildren(List children) {
		this.children = children;
	}

	public ElementIndex normalize(int index, List elementList,
			ParsingSession session) throws ParseException {
		int nestedIndex = 1;
		Element element = null;
		for (int i = index; i < elementList.size(); i++) {
			element = (Element) elementList.get(i);
			if (isStartElement(element)) {
				if (element instanceof NestableElement
						&& !((NestableElement) element).isFlat()) {
					nestedIndex++;
				}
			} else if (isEndElement(element)) {
				nestedIndex--;
				if (nestedIndex == 0) {
					return endMatchFound(index, element, elementList, session);
				}
			}
		}
		// no end element was found
		throw new ParseException(this, "no end element was found for '"
				+ this.toString() + "'");
	}

	protected ElementIndex endMatchFound(int startIndex, Element endMatch,
			List elementList, ParsingSession session) throws ParseException {
		List l = new ArrayList();
		Element topLevelElement = null;
		boolean foundEndelement = false;
		Element element = null;
		int nesting = 0;
		for (int i = startIndex; i < elementList.size(); i++) {
			element = (Element) elementList.get(i);
			if (element == endMatch) {
				foundEndelement = true;
				break;
			} else if (isStartElement(element)) {
				nesting++;
				l.add(element);
			} else if (isEndElement(element)) {
				nesting--;
				l.add(element);
			} else if (nesting == 0 && isTopLevelElement(element)) {
				if (null != topLevelElement) {
					ElementNormalizer.normalize(l, session, false);
					setTopLevelElements(new HeaderElementList(topLevelElement,
							l), session);
				} else {
					ElementNormalizer.normalize(l, session, false);
					setChildren(l);
				}
				topLevelElement = element;
				l = new ArrayList();
			} else {
				l.add(element);
			}
		}
		if (foundEndelement) {
			// remove grouped elements from list
			while (true) {
				if (endMatch == elementList.remove(startIndex)) {
					break;
				}
			}
			ElementNormalizer.normalize(l, session, false);
			if (null != topLevelElement) {
				setTopLevelElements(new HeaderElementList(topLevelElement, l),
						session);
			} else {
				setChildren(l);

			}
			return null;
		} else {
			throw new ParseException(this, "No end element found");
		}
	}

	protected void appendTemplateDefinedParameters(List children,
			ExtendedContext context, List macroInstanceList,
			MacroDirective macro, Map additionalContextEntries) {
		if (null != children) {
			for (Iterator j = children.iterator(); j.hasNext();) {
				Element e = (Element) j.next();
				if (e instanceof TemplateDefinedParameter) {
					TemplateDefinedParameter mid = (TemplateDefinedParameter) e;
					if (null != macro.getAttribute(mid.getName())) {
						macroInstanceList.add(new MacroInstanceEntity(
								(TemplateDefinedParameter) e, context,
								additionalContextEntries));
					}
// FIXME we are using the wrong context in this case - we need the context containing macro parameters
//				} else if (e instanceof MacroInstanceDirective) {
//					// might contain common template-defined parameters
//					MacroInstanceDirective mid = (MacroInstanceDirective) e;
//					if (null != mid.getMacroDefinition())
//						mid.getMacroDefinition()
//								.getMatchingTemplateDefinedParameters(context,
//										macroInstanceList, macro,
//										additionalContextEntries);
				} else if (e instanceof MacroInstanceAware) {
					((MacroInstanceAware) e)
							.getMatchingTemplateDefinedParameters(context,
									macroInstanceList, macro,
									additionalContextEntries);
				}
			}
		}
	}

	protected abstract boolean isStartElement(Element e);

	protected abstract boolean isEndElement(Element e);

	protected boolean isTopLevelElement(Element e) {
		return false;
	}

	protected void setTopLevelElements(HeaderElementList elements,
			ParsingSession parsingSession) throws ParseException {
	}

	protected boolean allowSelfNesting() {
		return true;
	}

	protected void appendElements(List elements, ExtendedContext context,
			Writer sw) throws ExecutionException {
		if (null != elements) {
			for (Iterator i = elements.iterator(); i.hasNext();) {
				((Element) i.next()).merge(context, sw);
			}
		}
	}

	public boolean isFlat() {
		return isFlat;
	}

	public void setFlat(boolean isFlat) {
		this.isFlat = isFlat;
	}
}