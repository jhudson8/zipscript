/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.directive.foreachdir;

import hudson.zipscript.parser.Constants;
import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.context.NestedContextWrapper;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.DebugElementContainerElement;
import hudson.zipscript.parser.template.element.DefaultElementFactory;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.NestableElement;
import hudson.zipscript.parser.template.element.PatternMatcher;
import hudson.zipscript.parser.template.element.comparator.InComparatorPatternMatcher;
import hudson.zipscript.parser.template.element.directive.LoopingDirective;
import hudson.zipscript.parser.template.element.directive.breakdir.BreakException;
import hudson.zipscript.parser.template.element.directive.breakdir.BreakableDirective;
import hudson.zipscript.parser.template.element.directive.continuedir.ContinueException;
import hudson.zipscript.parser.template.element.directive.continuedir.ContinueableDirective;
import hudson.zipscript.parser.template.element.directive.macrodir.MacroDirective;
import hudson.zipscript.parser.template.element.directive.macrodir.MacroInstanceAware;
import hudson.zipscript.parser.template.element.group.ListElement;
import hudson.zipscript.parser.template.element.lang.variable.SpecialVariableDefaultEelementFactory;
import hudson.zipscript.parser.template.element.lang.variable.VariableElement;
import hudson.zipscript.parser.template.element.lang.variable.adapter.RetrievalContext;
import hudson.zipscript.parser.template.element.lang.variable.adapter.SequenceAdapter;
import hudson.zipscript.parser.template.element.lang.variable.adapter.SequenceItem;
import hudson.zipscript.parser.template.element.special.InElement;
import hudson.zipscript.parser.template.element.special.InPatternMatcher;
import hudson.zipscript.parser.template.element.special.SpecialStringElement;

import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ForeachDirective extends NestableElement implements
		MacroInstanceAware, LoopingDirective, DebugElementContainerElement,
		BreakableDirective, ContinueableDirective {

	public static final String TOKEN_INDEX = "i";
	public static final String TOKEN_HASNEXT = "hasNext";

	private static PatternMatcher[] MATCHERS;
	static {
		PatternMatcher[] matchers = Constants.VARIABLE_MATCHERS;
		MATCHERS = new PatternMatcher[matchers.length];
		for (int i = 0; i < matchers.length; i++) {
			if (matchers[i] instanceof InComparatorPatternMatcher) {
				// replace with the standard in element
				MATCHERS[i] = new InPatternMatcher();
			} else {
				MATCHERS[i] = matchers[i];
			}
		}
	}

	private String varName;
	private Element sequenceElement;
	private SequenceAdapter sequenceAdapter;
	private boolean isInMacroDefinition;
	private List internalElements;

	public ForeachDirective(String contents, ParsingSession session,
			int contentPosition) throws ParseException {
		setParsingSession(session);
		parseContents(contents, session, contentPosition);
	}

	public List getInternalElements() {
		return internalElements;
	}

	private void parseContents(String contents, ParsingSession session,
			int contentPosition) throws ParseException {
		// see if we are in a macro definition
		for (Iterator i = session.getNestingStack().iterator(); i.hasNext();) {
			if (i.next() instanceof MacroDirective) {
				isInMacroDefinition = true;
				break;
			}
		}

		List elements = parseElements(contents, session, contentPosition);
		this.internalElements = elements;
		try {
			Element e = (Element) elements.get(0);
			if (e instanceof SpecialStringElement) {
				this.varName = ((SpecialStringElement) elements.get(0))
						.getTokenValue();
				elements.remove(0);
			} else {
				throw new ParseException(e,
						"Invalid sequence.  Expecting variable name");
			}
			e = (Element) elements.remove(0);
			if (!(e instanceof InElement))
				throw new ParseException(e,
						"Improperly formed for expression: 'in' should be second token '"
								+ this + "'");
			if (elements.size() == 1 && elements.get(0) instanceof ListElement) {
				// bypass element parsing
				this.sequenceElement = (Element) elements.get(0);
			} else {
				this.sequenceElement = new VariableElement(elements,
						RetrievalContext.SEQUENCE, this.varName, session);
			}
		} catch (IndexOutOfBoundsException e) {
			throw new ParseException(contentPosition,
					"Improperly formed for expression: must have at least 3 tokens");
		}
	}

	protected DefaultElementFactory getContentParsingDefaultElementFactory() {
		return SpecialVariableDefaultEelementFactory.INSTANCE;
	}

	public void getMatchingTemplateDefinedParameters(ExtendedContext context,
			List macroInstanceList, MacroDirective macro,
			Map additionalContextEntries) {
		Object sequence = sequenceElement.objectValue(context);
		if (null == sequence)
			throw new ExecutionException("Null sequence for '" + this + "'",
					this);
		try {
			if (null == sequenceAdapter || !sequenceAdapter.appliesTo(sequence)) {
				// set the sequence adapter
				sequenceAdapter = context.getParsingSession()
						.getResourceContainer().getVariableAdapterFactory()
						.getSequenceAdapter(sequence);
				if (null == sequenceAdapter) {
					// unknown sequence - just put the object in the context and
					// loop 1 time
					if (getParsingSession().isDebug()) {
						System.out.println("Executing: " + this.toString()
								+ " (0)");
					}
					context = new NestedContextWrapper(context, this);
					Integer index = new Integer(0);
					additionalContextEntries.put(TOKEN_INDEX, index);
					context.put(TOKEN_INDEX, index, false);
					additionalContextEntries.put(TOKEN_HASNEXT, Boolean.FALSE);
					context.put(TOKEN_HASNEXT, Boolean.FALSE, false);
					additionalContextEntries.put(varName, sequence);
					context.put(varName, sequence, false);
					try {
						appendTemplateDefinedParameters(getChildren(), context,
								macroInstanceList, macro,
								additionalContextEntries);
					} catch (ContinueException e) {
						// end
					}
					return;
				}
			}

			if (sequenceAdapter.hasNext(-1, null, sequence)) {
				context = new NestedContextWrapper(context, this);
				Integer index0 = new Integer(0);
				additionalContextEntries.put(TOKEN_INDEX, index0);
				context.put(TOKEN_INDEX, index0, false);
				additionalContextEntries.put(TOKEN_HASNEXT, Boolean.TRUE);
				context.put(TOKEN_HASNEXT, Boolean.TRUE, false);
				Object previousItem = null;
				int index = 0;
				boolean hasNext = true;
				while (true) {
					previousItem = sequenceAdapter.nextItem(index,
							previousItem, sequence);
					if (null != previousItem) {
						if (!sequenceAdapter.hasNext(index, previousItem, sequence)) {
							hasNext = false;
							context.put(TOKEN_HASNEXT, Boolean.FALSE, false);
							additionalContextEntries.put(TOKEN_HASNEXT,
									Boolean.FALSE);
						}
						if (previousItem instanceof SequenceItem) {
							Object obj = ((SequenceItem) previousItem).getObject();
							additionalContextEntries.put(varName, obj);
							context.put(varName, obj, false);
						} else {
							additionalContextEntries.put(varName, previousItem);
							context.put(varName, previousItem, false);
						}
						try {
							appendTemplateDefinedParameters(getChildren(), context,
									macroInstanceList, macro,
									additionalContextEntries);
						} catch (ContinueException e) {
							// continue
						}
					}
					if (!hasNext)
						break;
					Integer indexNext = new Integer(++index);
					additionalContextEntries.put(TOKEN_INDEX, indexNext);
					context.put(TOKEN_INDEX, indexNext, false);
				}
			}
		} catch (BreakException e) {
			// end
		}
	}

	public void merge(ExtendedContext context, Writer sw)
			throws ExecutionException {
		Object sequence = sequenceElement.objectValue(context);
		if (null == sequence)
			throw new ExecutionException("Null sequence for '" + this + "'",
					this);
		try {
			if (null == sequenceAdapter || !sequenceAdapter.appliesTo(sequence)) {
				// set the sequence adapter
				sequenceAdapter = context.getParsingSession()
						.getResourceContainer().getVariableAdapterFactory()
						.getSequenceAdapter(sequence);
				if (null == sequenceAdapter) {
					// unknown sequence - just put the object in the context and
					// loop 1 time
					if (getParsingSession().isDebug()) {
						System.out.println("Executing: " + this.toString()
								+ " (0)");
					}
					context = new NestedContextWrapper(context, this);
					context.put(TOKEN_INDEX, new Integer(0), false);
					context.put(TOKEN_HASNEXT, Boolean.FALSE, false);
					context.put(varName, sequence, false);
					try {
						appendElements(getChildren(), context, sw);
					} catch (ContinueException e) {
						// end
					}
					return;
				}
			}

			if (sequenceAdapter.hasNext(-1, null, sequence)) {
				context = new NestedContextWrapper(context, this);
				context.put(TOKEN_INDEX, new Integer(0), false);
				context.put(TOKEN_HASNEXT, Boolean.TRUE, false);
				Object previousItem = null;
				int index = 0;
				boolean hasNext = true;
				while (true) {
					previousItem = sequenceAdapter.nextItem(index,
							previousItem, sequence);
					if (null != previousItem) {
						if (!sequenceAdapter.hasNext(index, previousItem, sequence)) {
							hasNext = false;
							context.put(TOKEN_HASNEXT, Boolean.FALSE, false);
						}
						if (previousItem instanceof SequenceItem) {
							context.put(varName, ((SequenceItem) previousItem)
									.getObject(), false);
						} else {
							context.put(varName, previousItem, false);
						}
						try {
							appendElements(getChildren(), context, sw);
						} catch (ContinueException e) {
							// continue
						}
					}
					if (!hasNext)
						break;
					context.put(TOKEN_INDEX, new Integer(++index), false);
				}
			}
		} catch (BreakException e) {
			// end
		}
	}

	protected PatternMatcher[] getContentParsingPatternMatchers() {
		return MATCHERS;
	}

	protected boolean isStartElement(Element e) {
		return (e instanceof ForeachDirective);
	}

	protected boolean isEndElement(Element e) {
		return (e instanceof EndForeachDirective);
	}

	public String toString() {
		if (null != sequenceElement)
			return "[#foreach " + varName + " in " + sequenceElement + "]";
		else
			return "[#foreach " + varName + " in ?]";
	}

	public boolean isInMacroDefinition() {
		return isInMacroDefinition;
	}
}