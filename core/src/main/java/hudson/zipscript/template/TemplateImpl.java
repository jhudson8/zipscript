/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.template;

import hudson.zipscript.ResourceContainer;
import hudson.zipscript.parser.context.Context;
import hudson.zipscript.parser.context.ContextWrapperFactory;
import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ElementIndex;
import hudson.zipscript.parser.template.data.ParsingResult;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.ToStringWithContextElement;
import hudson.zipscript.parser.template.element.directive.initialize.InitializeDirective;
import hudson.zipscript.parser.template.element.directive.macrodir.MacroInstanceDirective;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class TemplateImpl implements Template, Evaluator, Element,
		ToStringWithContextElement {

	private Element element;
	private List elements;
	private List initializeElements;
	private ParsingSession parsingSession;
	private ParsingResult parsingResult;
	private ResourceContainer resourceContainer;

	public TemplateImpl(List elements, ParsingSession parsingSession,
			ParsingResult result) {
		this.elements = elements;
		this.parsingSession = parsingSession;
		this.parsingResult = result;
		// load elements for initialization
		initializeElements = new ArrayList();
		for (Iterator i = elements.iterator(); i.hasNext();) {
			loadInitializeElements((Element) i.next(), initializeElements);
		}
	}

	public TemplateImpl(Element element, ParsingSession parsingSession) {
		this.element = element;
		this.parsingSession = parsingSession;
	}

	public Context initialize(Object context) throws ExecutionException {
		return initialize(context, null);
	}

	public Context initialize(Object obj, Locale locale)
			throws ExecutionException {
		ExtendedContext context = getContext(obj, locale);
		if (!context.isInitialized(this)) {
			for (Iterator i = initializeElements.iterator(); i.hasNext();) {
				((InitializeDirective) i.next()).doInitialize(context);
			}
			context.markInitialized(this);
		}
		return context;
	}

	private void loadInitializeElements(Element e, List l) {
		if (e instanceof InitializeDirective) {
			l.add(e);
		} else {
			if (e instanceof MacroInstanceDirective) {
				MacroInstanceDirective mid = (MacroInstanceDirective) e;
				if (null != mid.getNamespace()
						&& null != mid.getMacroDefinition()) {
					// add macro definition initialization
					loadInitializeMacroLibElements(mid.getMacroDefinition(), l);
				}
			}
			List children = e.getChildren();
			if (null != children) {
				for (Iterator i = children.iterator(); i.hasNext();) {
					loadInitializeElements((Element) i.next(), l);
				}
			}
		}
	}

	private void loadInitializeMacroLibElements(Element e, List l) {
		if (e instanceof InitializeDirective) {
			l.add(e);
		} else {
			if (e instanceof MacroInstanceDirective) {
				MacroInstanceDirective mid = (MacroInstanceDirective) e;
				if (null != mid.getMacroDefinition()) {
					// add macro definition initialization
					loadInitializeMacroLibElements(mid.getMacroDefinition(), l);
				}
			}
			List children = e.getChildren();
			if (null != children) {
				for (Iterator i = children.iterator(); i.hasNext();) {
					loadInitializeElements((Element) i.next(), l);
				}
			}
		}
	}

	/** Template Methods * */
	public boolean booleanValue(Object context) throws ExecutionException {
		return booleanValue(context, null);
	}

	public boolean booleanValue(Object context, Locale locale)
			throws ExecutionException {
		return booleanValue(getContext(context, locale));
	}

	public Object objectValue(Object context) throws ExecutionException {
		return objectValue(context, null);
	}

	public Object objectValue(Object context, Locale locale)
			throws ExecutionException {
		return objectValue(getContext(context, locale));
	}

	public String merge(Object context) throws ExecutionException {
		return merge(context, (Locale) null);
	}

	public String merge(Object context, Locale locale)
			throws ExecutionException {
		StringWriter sw = new StringWriter();
		merge(context, sw, locale);
		return sw.toString();
	}

	public void merge(Object context, Writer sw) throws ExecutionException {
		merge(context, sw, null);
	}

	public void merge(Object obj, Writer sw, Locale locale)
			throws ExecutionException {
		ExtendedContext context = (ExtendedContext) initialize(obj, locale);
		merge(context, sw);
	}

	/** Element Methods * */
	public void merge(ExtendedContext context, Writer sw)
			throws ExecutionException {
		try {
			for (Iterator i = elements.iterator(); i.hasNext();) {
				((Element) i.next()).merge(context, sw);
			}
		} catch (ExecutionException e) {
			e.setParsingResult(parsingResult);
			throw (e);
		}
	}

	public boolean booleanValue(ExtendedContext context)
			throws ExecutionException {
		if (null != element)
			try {
				return element.booleanValue(context);
			} catch (ExecutionException e) {
				e.setParsingResult(parsingResult);
				throw (e);
			}
		else
			throw new ExecutionException("Invalid boolean expression", null);
	}

	public Object objectValue(ExtendedContext context)
			throws ExecutionException {
		if (null != element)
			try {
				return element.objectValue(context);
			} catch (ExecutionException e) {
				e.setParsingResult(parsingResult);
				throw (e);
			}
		else
			throw new ExecutionException("Invalid object expression", null);
	}

	public int getElementLength() {
		return 0;
	}

	public long getElementPosition() {
		return 0;
	}

	public ElementIndex normalize(int index, List elementList,
			ParsingSession session) throws ParseException {
		return null;
	}

	public void setElementLength(int length) {
	}

	public void setElementPosition(long position) {
	}

	private ExtendedContext getContext(Object obj, Locale locale) {
		if (obj instanceof ExtendedContext
				&& ((ExtendedContext) obj).isInitialized(this))
			return (ExtendedContext) obj;
		ExtendedContext context = ContextWrapperFactory.getInstance().wrap(obj,
				parsingSession, resourceContainer);
		context.setResourceContainer(resourceContainer);
		context.setParsingSession(parsingSession);
		if (null != locale)
			context.setLocale(locale);
		return context;
	}

	public ParsingSession getParsingSession() {
		return parsingSession;
	}

	public ParsingResult getParsingResult() {
		return parsingResult;
	}

	public Element getElement() {
		return element;
	}

	public List getElements() {
		return elements;
	}

	public void validate(ParsingSession session) throws ParseException {
	}

	public List getChildren() {
		if (null != elements)
			return elements;
		else if (null != element) {
			elements = new ArrayList();
			elements.add(element);
			return elements;
		} else
			return null;
	}

	public ResourceContainer getResourceContainer() {
		return resourceContainer;
	}

	public void setResourceContainer(ResourceContainer resourceContainer) {
		this.resourceContainer = resourceContainer;
	}

	public void append(ExtendedContext context, Writer writer) {
		if (context.isInitialized(this)) {
			for (Iterator i = initializeElements.iterator(); i.hasNext();) {
				((InitializeDirective) i.next()).doInitialize(context);
			}
			context.markInitialized(this);
		}
		merge(context, writer);
	}

	public String toString(ExtendedContext context) {
		StringWriter sw = new StringWriter();
		append(context, sw);
		return sw.toString();
	}
}