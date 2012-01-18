/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.directive.macrodir;

import hudson.zipscript.parser.Constants;
import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.context.NestedContextWrapper;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.DebugElementContainerElement;
import hudson.zipscript.parser.template.element.DefaultElementFactory;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.ElementAttribute;
import hudson.zipscript.parser.template.element.NestableElement;
import hudson.zipscript.parser.template.element.PatternMatcher;
import hudson.zipscript.parser.template.element.ToStringWithContextElement;
import hudson.zipscript.parser.template.element.group.MapElement;
import hudson.zipscript.parser.template.element.lang.AssignmentElement;
import hudson.zipscript.parser.template.element.lang.TextElement;
import hudson.zipscript.parser.template.element.lang.variable.adapter.RetrievalContext;
import hudson.zipscript.parser.template.element.special.NoMapDefaultVariablePatternMatcher;
import hudson.zipscript.parser.template.element.special.RequiredIdentifierPatternMatcher;
import hudson.zipscript.parser.template.element.special.SpecialStringElement;
import hudson.zipscript.resource.macrolib.MacroLibrary;

import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MacroDirective extends NestableElement implements
		MacroInstanceAware, DebugElementContainerElement {

	private static PatternMatcher[] MATCHERS;
	static {
		PatternMatcher[] matchers = Constants.VARIABLE_MATCHERS;
		MATCHERS = new PatternMatcher[matchers.length + 1];
		MATCHERS[0] = new RequiredIdentifierPatternMatcher();
		System.arraycopy(matchers, 0, MATCHERS, 1, matchers.length);
	}

	private String contents;
	private String name;
	private List attributes = new ArrayList();
	private Map attributeMap = new HashMap();
	private MacroLibrary macroLibrary;
	// debug
	private List internalElements;

	public MacroDirective(String contents, ParsingSession session,
			int contentPosition) throws ParseException {
		this.contents = contents;
		setParsingSession(session);
		parseContents(contents, session, contentPosition);
		session.addInlineMacroDefinition(this);
	}

	public List getInternalElements() {
		return internalElements;
	}

	protected void parseContents(String contents, ParsingSession session,
			int contentPosition) throws ParseException {
		contents = contents.trim();
		String[] nameAndIdentifiers = null;
		String attributes = null;
		int index = contents.indexOf('|');
		if (index >= 0) {
			nameAndIdentifiers = contents.substring(0, index).split(" ");
			attributes = contents.substring(index + 1).trim();
		} else {
			nameAndIdentifiers = contents.split(" ");
		}

		if (nameAndIdentifiers.length == 0)
			throw new ParseException(this,
					"Macro definition does not have a name");
		// evaluate name and identifiers
		index = 0;
		name = nameAndIdentifiers[index++];
		// validate name
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (!(Character.isLetterOrDigit(c) || c == '_' || c == '-'))
				throw new ParseException(this, "Invalid macro name '" + name
						+ "'");
		}

		// we aren't supporting any new identifiers right now
		if (nameAndIdentifiers.length > index)
			throw new ParseException(this, "Unknown macro identifier '"
					+ nameAndIdentifiers[1] + "' did you forget to add the separator '|'?");

		boolean startedRequiredAttributes = false;
		boolean startedDefaultedAttributes = false;
		if (null != attributes && attributes.length() > 0) {
			List elements = parseElements(attributes, session, contentPosition);
			this.internalElements = elements;
			while (true) {
				MacroDefinitionAttribute attribute = getAttribute(elements,
						session);
				if (null == attribute)
					break;
				if (attribute.isTemplateDefinedParameter()) {
					if (startedRequiredAttributes || startedDefaultedAttributes)
						throw new ParseException(
								this,
								"All template-defined parameters must be referenced before standard parameters '"
										+ attribute.getName() + "'");
				} else if (null == attribute.getDefaultValue()) {
					if (startedDefaultedAttributes)
						throw new ParseException(this,
								"All required parameters must be referenced before defaulted parameters '"
										+ attribute.getName() + "'");
					startedRequiredAttributes = true;
				} else {
					startedDefaultedAttributes = true;
				}
				this.attributes.add(attribute);
			}
			for (Iterator i = getAttributes().iterator(); i.hasNext();) {
				MacroDefinitionAttribute attr = (MacroDefinitionAttribute) i
						.next();
				attributeMap.put(attr.getName(), attr);
			}
		}
	}

	protected PatternMatcher[] getContentParsingPatternMatchers() {
		return MATCHERS;
	}

	public MacroDefinitionAttribute getAttribute(String name) {
		return (MacroDefinitionAttribute) attributeMap.get(name);
	}

	protected MacroDefinitionAttribute getAttribute(List elements,
			ParsingSession session) throws ParseException {
		String name = null;
		Element defaultVal = null;
		boolean required = false;
		if (elements.size() == 0)
			return null;
		Element e;
		e = (Element) elements.remove(0);
		if (e instanceof SpecialStringElement)
			name = ((SpecialStringElement) e).getTokenValue();
		else if (e instanceof TextElement)
			name = ((TextElement) e).getText();
		else
			throw new ParseException(this,
					"Unexpected element, expecting macro attribute name.  Found '"
							+ e + "'");
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (!(Character.isLetterOrDigit(c) || c == '_' || c == '-'))
				throw new ParseException(this, "Invalid macro attribute name '"
						+ name + "'");
		}

		// attribute properties
		if (elements.size() > 0) {
			e = (Element) elements.get(0);
			if (e instanceof MapElement) {
				elements.remove(0);
				// it is a template defined parameter definition
				List children = ((MapElement) e).getChildren();
				if (null == children || children.size() == 0) {
					return new MacroDefinitionAttribute(name, new ArrayList(0),
							required);
				} else {
					List tldAttributes = new ArrayList();
					while (true) {
						MacroDefinitionAttribute attribute = getAttribute(
								children, session);
						if (null == attribute)
							break;
						else
							tldAttributes.add(attribute);
					}
					return new MacroDefinitionAttribute(name, tldAttributes,
							required);
				}
			} else if (e instanceof AssignmentElement) {
				elements.remove(0);
				if (elements.size() == 0) {
					throw new ParseException(this, "Unexpected content '" + e
							+ "'");
				} else {
					// default
					e = (Element) elements.get(0);
					if (e instanceof AssignmentElement) {
						throw new ParseException(this, "Unexpected content '"
								+ e + "'");
					} else {
						elements.remove(0);
						defaultVal = e;
					}
				}
			} else {
				required = true;
			}
		}
		MacroDefinitionAttribute attribute = new MacroDefinitionAttribute(name,
				defaultVal, required);
		return attribute;
	}

	public void executeMacro(ExtendedContext context, boolean isOrdinal,
			List attributes, MacroInstanceExecutor nestedContent, Writer sw)
			throws ExecutionException {
		if (getParsingSession().isDebug()) {
			System.out.println("Executing: macro '" + getName() + "'");
			for (Iterator i = attributes.iterator(); i.hasNext();) {
				System.out.println("\t" + i.next());
			}
		}

		ExtendedContext parentContext = context;
		context = new NestedContextWrapper(context, this, false);
		// add attributes to context
		if (isOrdinal) {
			for (int i = 0; i < attributes.size(); i++) {
				MacroDefinitionAttribute defAttribute = (MacroDefinitionAttribute) getAttributes()
						.get(i);
				ElementAttribute instAttribute = (ElementAttribute) attributes
						.get(i);
				Object val = instAttribute.getValue()
						.objectValue(parentContext);
				if (null == val) {
					// do we default
					if (null != defAttribute.getDefaultValue())
						val = defAttribute.getDefaultValue();
				}
				if (val instanceof ToStringWithContextElement)
					val = ((ToStringWithContextElement) val).toString(context);
				if (null != val)
					context.put(defAttribute.getName(), val, true);
			}
		} else {
			for (int i = 0; i < attributes.size(); i++) {
				ElementAttribute instAttribute = (ElementAttribute) attributes
						.get(i);
				Object val = instAttribute.getValue()
						.objectValue(parentContext);
				if (null != val)
					context.put(instAttribute.getName(), val, true);
			}
			// check the defaults
			for (Iterator i = getAttributes().iterator(); i.hasNext();) {
				MacroDefinitionAttribute defAttribute = (MacroDefinitionAttribute) i
						.next();
				if (null != defAttribute.getDefaultValue()
						&& null == context.get(defAttribute.getName(),
								RetrievalContext.UNKNOWN, null)) {
					Object val = defAttribute.getDefaultValue().objectValue(context);
					if (val instanceof ToStringWithContextElement)
						val = ((ToStringWithContextElement) val).toString(context);
					if (null != val)
						context.put(defAttribute.getName(), val, true);
				}
			}
		}

		if (!nestedContent.getMacroInstance().isBodyEmpty())
			context.put(Constants.BODY, nestedContent, true);
		if (null != nestedContent.getMacroInstance().getHeader())
			context.put(Constants.HEADER, nestedContent.getMacroInstance()
					.getHeader(), true);
		if (null != nestedContent.getMacroInstance().getFooter())
			context.put(Constants.FOOTER, nestedContent.getMacroInstance()
					.getFooter(), true);
		MacroInstanceEntity mie = new MacroInstanceEntity(nestedContent
				.getMacroInstance(), context, null);
		context.put(Constants.THIS, mie, true);
		context.put(Constants.VARS, context);

		// add template defined parameters
		if (getParsingSession().isDebug()) {
			System.out.println("Preparing: " + nestedContent.getMacroInstance()
					+ " Substructure");
		}
		List tdp = new ArrayList();
		if (mie.getMacroInstance().isInMacroDefinition()) {
			appendTemplateDefinedParameters(nestedContent.getChildren(),
					parentContext, tdp, this, new HashMap());
		} else {
			// we are in the body of a macro which is in a normal template -
			// simulate the context
			// ((NestedContextWrapper) context).setTravelUp(true);
			appendTemplateDefinedParameters(nestedContent.getChildren(),
					parentContext, tdp, this, new HashMap());
			// ((NestedContextWrapper) context).setTravelUp(false);
		}
		if (getParsingSession().isDebug()) {
			for (Iterator i = tdp.iterator(); i.hasNext();) {
				System.out.println("\t" + i.next());
			}
		}

		for (Iterator i = tdp.iterator(); i.hasNext();) {
			mie = (MacroInstanceEntity) i.next();
			Object obj = context.get(mie.getMacroInstance().getName(),
					RetrievalContext.UNKNOWN, null);
			if (null == obj) {
				context.put(mie.getMacroInstance().getName(), mie, true);
			} else if (obj instanceof List) {
				((List) obj).add(mie);
			} else if (obj instanceof MacroInstanceEntity) {
				List l = new ArrayList();
				l.add(obj);
				l.add(mie);
				context.put(mie.getMacroInstance().getName(), l, true);
			} else {
				context.put(mie.getMacroInstance().getName(), mie, true);
			}
		}

		// execute macro
		for (Iterator i = getChildren().iterator(); i.hasNext();) {
			((Element) i.next()).merge(context, sw);
		}
	}

	public void getMatchingTemplateDefinedParameters(ExtendedContext context,
			List macroInstanceList, MacroDirective macro,
			Map additionalContextEntries) {
		appendTemplateDefinedParameters(getChildren(), context,
				macroInstanceList, macro, additionalContextEntries);
	}

	public MacroDefinitionAttribute getTemplateDefinedParameterAttribute(
			String name) {
		MacroDefinitionAttribute rtn = null;
		for (Iterator i = attributes.iterator(); i.hasNext();) {
			rtn = getTemplateDefinedParameterAttribute(name,
					(MacroDefinitionAttribute) i.next());
			if (null != rtn)
				return rtn;
		}
		return null;
	}

	private MacroDefinitionAttribute getTemplateDefinedParameterAttribute(
			String name, MacroDefinitionAttribute attr) {
		MacroDefinitionAttribute rtn = null;
		if (attr.isTemplateDefinedParameter()) {
			if (attr.getName().equals(name))
				return attr;
			else {
				for (Iterator i = attr.getTDPAttributes().iterator(); i
						.hasNext();) {
					rtn = getTemplateDefinedParameterAttribute(name,
							(MacroDefinitionAttribute) i.next());
					if (null != rtn)
						return rtn;
				}
			}
		}
		return null;
	}

	protected DefaultElementFactory getContentParsingDefaultElementFactory() {
		return NoMapDefaultVariablePatternMatcher.getInstance();
	}

	protected boolean isStartElement(Element e) {
		return (e instanceof MacroDirective);
	}

	protected boolean isEndElement(Element e) {
		return (e instanceof EndMacroDirective);
	}

	protected boolean allowSelfNesting() {
		return false;
	}

	public String toString() {
		return "[#macro " + contents + "]";
	}

	public void merge(ExtendedContext context, Writer sw) {
	}

	public String getContents() {
		return contents;
	}

	public String getName() {
		return name;
	}

	public List getAttributes() {
		return attributes;
	}

	public Map getAttributeMap() {
		return attributeMap;
	}

	public MacroLibrary getMacroLibrary() {
		return macroLibrary;
	}

	public void setMacroLibrary(MacroLibrary macroLibrary) {
		this.macroLibrary = macroLibrary;
	}
}
