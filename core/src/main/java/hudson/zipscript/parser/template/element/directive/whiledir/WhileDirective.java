/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.directive.whiledir;

import hudson.zipscript.parser.Constants;
import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.context.NestedContextWrapper;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.DebugElementContainerElement;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.NestableElement;
import hudson.zipscript.parser.template.element.directive.LoopingDirective;
import hudson.zipscript.parser.template.element.directive.breakdir.BreakException;
import hudson.zipscript.parser.template.element.directive.breakdir.BreakableDirective;
import hudson.zipscript.parser.template.element.directive.continuedir.ContinueException;
import hudson.zipscript.parser.template.element.directive.continuedir.ContinueableDirective;
import hudson.zipscript.parser.template.element.directive.macrodir.MacroDirective;
import hudson.zipscript.parser.template.element.directive.macrodir.MacroInstanceAware;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class WhileDirective extends NestableElement implements
		MacroInstanceAware, LoopingDirective, DebugElementContainerElement,
		BreakableDirective, ContinueableDirective {

	public static final String TOKEN_INDEX = "i";

	private Element whileElement;
	private boolean isInMacroDefinition;

	public WhileDirective(String contents, int contentIndex,
			ParsingSession parsingSession) throws ParseException {
		parseContents(contents, contentIndex, parsingSession);
	}

	public List getInternalElements() {
		List list = new ArrayList();
		list.add(whileElement);
		return list;
	}

	private void parseContents(String s, int contentIndex,
			ParsingSession session) throws ParseException {
		// see if we are in a macro definition
		for (Iterator i = session.getNestingStack().iterator(); i.hasNext();) {
			if (i.next() instanceof MacroDirective) {
				isInMacroDefinition = true;
				break;
			}
		}

		whileElement = parseElement(s, session, contentIndex);
	}

	public void merge(ExtendedContext context, Writer sw)
			throws ExecutionException {
		int i = 0;
		ExtendedContext subContext = new NestedContextWrapper(context, this);
		subContext.put(Constants.SUPER, context);
		subContext.put(TOKEN_INDEX, new Integer(0), false);
		try {
			while (whileElement.booleanValue(subContext)) {
				try {
					appendElements(getChildren(), subContext, sw);
				} catch (ContinueException e) {
					// continue
				}
				subContext.put(TOKEN_INDEX, new Integer(++i), false);
			}
		} catch (BreakException e) {
			// end
		}
	}

	public void getMatchingTemplateDefinedParameters(ExtendedContext context,
			List macroInstanceList, MacroDirective macro,
			Map additionalContextEntries) throws ExecutionException {
		int i = 0;
		ExtendedContext subContext = new NestedContextWrapper(context, this);
		subContext.put(Constants.SUPER, context);
		Integer int0 = new Integer(0);
		additionalContextEntries.put(TOKEN_INDEX, int0);
		subContext.put(TOKEN_INDEX, int0, false);
		try {
			while (whileElement.booleanValue(subContext)) {
				try {
					appendTemplateDefinedParameters(getChildren(), subContext,
							macroInstanceList, macro, additionalContextEntries);
				} catch (ContinueException e) {
					// continue
				}
				Integer index = new Integer(++i);
				additionalContextEntries.put(TOKEN_INDEX, index);
				subContext.put(TOKEN_INDEX, index, false);
			}
		} catch (BreakException e) {
			// end
		}
	}

	protected boolean isStartElement(Element e) {
		return (e instanceof WhileDirective);
	}

	protected boolean isEndElement(Element e) {
		return (e instanceof EndWhileDirective);
	}

	public String toString() {
		return "[#while " + whileElement.toString() + "]";
	}

	public boolean isInMacroDefinition() {
		return isInMacroDefinition;
	}
}
