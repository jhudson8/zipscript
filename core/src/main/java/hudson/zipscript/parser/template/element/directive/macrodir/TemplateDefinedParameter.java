/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.directive.macrodir;

import hudson.zipscript.parser.Constants;
import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ElementIndex;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.DebugElementContainerElement;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.ElementAttribute;
import hudson.zipscript.parser.template.element.NestableElement;
import hudson.zipscript.parser.template.element.NonOutputElement;
import hudson.zipscript.parser.template.element.lang.AssignmentElement;
import hudson.zipscript.parser.template.element.lang.TextElement;
import hudson.zipscript.parser.template.element.special.SpecialStringElement;
import hudson.zipscript.parser.util.AttributeUtil;
import hudson.zipscript.parser.util.PropertyUtil;
import hudson.zipscript.parser.util.StringUtil;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TemplateDefinedParameter extends NestableElement implements
		MacroInstanceAware, NonOutputElement, DebugElementContainerElement,
		MacroOrientedElement {

	private boolean isOrdinal = true;
	private String contents;
	private String name;

	private List attributes = new ArrayList();
	private Map attributeMap;

	private MacroInstanceDirective baseMacroInstance;
	boolean isInMacroDefinition;
	boolean isInTemplate;
	private MacroDefinitionAttribute templateDefinedParameterDefinition;
	// for a template defined parameter reference inside a common macro
	boolean isTemplateDefinedParameterInMacroDefinition;
	private boolean isBodyEmpty;

	// text normalization
	TextElement previousTextElement;

	// debug
	private List internalElements;

	public TemplateDefinedParameter(String contents, boolean isFlat,
			ParsingSession parsingSession, int contentPosition)
			throws ParseException {
		this(contents, isFlat, false, parsingSession, contentPosition);
	}

	public TemplateDefinedParameter(String contents, boolean isFlat,
			boolean isTemplateDefinedParamterInMacroDefinition,
			ParsingSession parsingSession, int contentPosition)
			throws ParseException {
		this.contents = contents;
		setFlat(isFlat);
		this.isTemplateDefinedParameterInMacroDefinition = isTemplateDefinedParamterInMacroDefinition;
		parseContents(contents, parsingSession, contentPosition);
	}

	public List getInternalElements() {
		return internalElements;
	}

	protected void parseContents(String contents, ParsingSession session,
			int contentPosition) throws ParseException {
		List elements = parseElements(contents, session, contentPosition);
		this.internalElements = new ArrayList();
		internalElements.addAll(elements);
		if (elements.size() == 0)
			throw new ParseException(this,
					"Template-defined parameter name was not specified");
		Element e;
		e = (Element) elements.remove(0);
		if (e instanceof SpecialStringElement)
			name = ((SpecialStringElement) e).getTokenValue();
		// validate name
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (!(Character.isLetterOrDigit(c) || c == '_' || c == '-' || c == Constants.NAMESPACE_SEPARATOR))
				throw new ParseException(contentPosition,
						"Invalid template-defined parameter name '" + name
								+ "'");
		}
		int index = name.indexOf(Constants.NAMESPACE_SEPARATOR);
		if (index > 0) {
			throw new ParseException(this,
					"Namespaces are not allowed inside template-defined parameters");
		}

		// determine parameter type
		if (elements.size() != 0) {
			if (elements.size() == 1)
				isOrdinal = true;
			else if (elements.get(1) instanceof AssignmentElement) {
				isOrdinal = false;
				attributeMap = new HashMap();
			}

			// look for attributes
			while (true) {
				ElementAttribute attribute = AttributeUtil.getAttribute(
						elements, session, this);
				if (null == attribute)
					break;
				else {
					this.attributes.add(attribute);
					if (!isOrdinal)
						attributeMap.put(attribute.getName(), attribute);
				}
			}
		} else {
			isOrdinal = false;
		}
	}

	public void validate(ParsingSession session) throws ParseException {
		// see if we are in a macro definition
		for (Iterator i = session.getNestingStack().iterator(); i.hasNext();) {
			Element e = (Element) i.next();
			if (e instanceof MacroDirective) {
				isInMacroDefinition = true;
				break;
			}
		}
		if (session.getParsingContext() == ParsingSession.PARSING_CONTEXT_TEMPLATE)
			this.isInTemplate = true;
		if (!isTemplateDefinedParameterInMacroDefinition) {
			// find associated macro instance
			for (int i = session.getNestingStack().size() - 1; i >= 0; i--) {
				Element e = (Element) session.getNestingStack().get(i);
				if (e instanceof MacroInstanceDirective) {
					baseMacroInstance = (MacroInstanceDirective) e;
					break;
				}
			}
			if (null == baseMacroInstance) {
				throw new ParseException(this,
						"Found template-defined parameter '" + this
								+ "' with no macro parent");
			}
		}

		if (isTemplateDefinedParameterInMacroDefinition && !isInMacroDefinition) {
			throw new ParseException(this,
					"the '[.%' syntax can only be used inside a macro definition");
		}

		if (isOrdinal()) {
			throw new ParseException(this,
					"Template-defined parameters must have named parameters");
		}
		if (isTemplateDefinedParameterInMacroDefinition) {
			// lazy load - no validation here
		} else {
			MacroDefinitionAttribute attr = null;
			if (null != baseMacroInstance
					&& null != baseMacroInstance.getMacroDefinition())
				attr = baseMacroInstance.getMacroDefinition()
						.getTemplateDefinedParameterAttribute(getName());
			if (null == attr) {
				// we can't find any matching defined template parameters
				throw new ParseException(this, "'" + getName()
						+ "' is not a defined template parameter");
			} else {
				// it is a template defined parameter
				this.templateDefinedParameterDefinition = attr;
				List attributes = attr.getTDPAttributes();
				// make sure all required and/or defaulted attributes are
				// defined
				for (int i = 0; i < attributes.size(); i++) {
					MacroDefinitionAttribute attribute = (MacroDefinitionAttribute) attributes
							.get(i);
					validateTemplateAttribute(attribute, this);
				}
				if (null != previousTextElement)
					StringUtil.trimLastEmptyLine(previousTextElement);
			}
		}

		if (null != getChildren()
				&& PropertyUtil.getProperty(Constants.TRIM_MACRO_BODY, true,
						session.getParameters().getInitParameters())) {
			// trim the body
			this.isBodyEmpty = StringUtil.trim(getChildren());
		}
	}

	protected void validateTemplateAttribute(
			MacroDefinitionAttribute attribute, TemplateDefinedParameter mid)
			throws ParseException {
		if (attribute.isTemplateDefinedParameter()) {
			// make sure we have the parameter
			List macroInstanceDirectives = new ArrayList();
			loadTemplateDefinedAttributes(attribute.getName(), mid,
					macroInstanceDirectives);
			if (macroInstanceDirectives.size() == 0 && attribute.isRequired())
				throw new ParseException(mid,
						"Missing template defined parameter '"
								+ attribute.getName() + "'");
			for (Iterator i = macroInstanceDirectives.iterator(); i.hasNext();) {
				TemplateDefinedParameter subMid = (TemplateDefinedParameter) i
						.next();
				for (Iterator j = attribute.getTDPAttributes().iterator(); j
						.hasNext();) {
					MacroDefinitionAttribute mda = (MacroDefinitionAttribute) j
							.next();
					if (mda.isTemplateDefinedParameter()) {
						validateTemplateAttribute(mda, subMid);
					}
				}
			}
		} else {
			if (attribute.isRequired()
					&& null == mid.getAttribute(attribute.getName()))
				throw new ParseException(mid,
						"Missing template defined parameter attribute '"
								+ attribute.getName() + "'");
		}
	}

	protected void loadTemplateDefinedAttributes(String name, Element e, List l) {
		List children = e.getChildren();
		if (null != e.getChildren()) {
			for (Iterator i = children.iterator(); i.hasNext();) {
				Element subE = (Element) i.next();
				if (subE instanceof TemplateDefinedParameter) {
					TemplateDefinedParameter mid = (TemplateDefinedParameter) subE;
					if (mid.getName().equals(name))
						l.add(mid);
				} else
					loadTemplateDefinedAttributes(name, subE, l);
			}
		}
	}

	public ElementAttribute getAttribute(String name) {
		if (null == attributeMap)
			return null;
		else
			return (ElementAttribute) attributeMap.get(name);
	}

	public ElementIndex normalize(int index, List elementList,
			ParsingSession session) throws ParseException {
		ElementIndex rtn = null;
		if (isFlat())
			rtn = null;
		else
			rtn = super.normalize(index, elementList, session);
		StringUtil.trimLastEmptyLine(getChildren());
		if (index > 0 && elementList.get(index - 1) instanceof TextElement)
			previousTextElement = (TextElement) elementList.get(index - 1);
		return rtn;
	}

	protected boolean isStartElement(Element e) {
		return (e instanceof TemplateDefinedParameter && ((TemplateDefinedParameter) e)
				.getName().equals(getName()));
	}

	protected boolean isEndElement(Element e) {
		if (e instanceof EndTemplateDefinedParameter) {
			EndTemplateDefinedParameter eid = (EndTemplateDefinedParameter) e;
			if (eid.getName().equals(getName())
					&& eid.isTemplateDefinedParameterInMacroDefinition() == isTemplateDefinedParameterInMacroDefinition())
				return true;
		}
		return false;
	}

	protected boolean allowSelfNesting() {
		return false;
	}

	public String toString() {
		if (this.isTemplateDefinedParameterInMacroDefinition)
			return "[.%" + contents + "]";
		else
			return "[%" + contents + "]";
	}

	public void merge(ExtendedContext context, Writer sw)
			throws ExecutionException {
		// no merging for a template-defined parameter
	}

	public String getNestedContent(ExtendedContext context)
			throws ExecutionException {
		if (isInTemplate()) {
			context = context.getTemplateContext();
		}
		StringWriter sw = new StringWriter();
		for (Iterator i = getChildren().iterator(); i.hasNext();) {
			((Element) i.next()).merge(context, sw);
		}
		return sw.toString();
	}

	public void writeNestedContent(ExtendedContext context, Writer writer)
			throws ExecutionException {
		for (Iterator i = getChildren().iterator(); i.hasNext();) {
			((Element) i.next()).merge(context, writer);
		}
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

	public boolean isOrdinal() {
		return isOrdinal;
	}

	public void setOrdinal(boolean isOrdinal) {
		this.isOrdinal = isOrdinal;
	}

	public void getMatchingTemplateDefinedParameters(ExtendedContext context,
			List list, MacroDirective macro, Map additionalContextEntries) {
		if (isTemplateDefinedParameterInMacroDefinition
				&& null != macro.getAttribute(getName())) {
			list.add(new MacroInstanceEntity((TemplateDefinedParameter) this,
					context, additionalContextEntries));
		} else {
			super.appendTemplateDefinedParameters(getChildren(), context, list,
					macro, additionalContextEntries);
		}
	}

	public boolean isInMacroDefinition() {
		return isInMacroDefinition;
	}

	public boolean isInTemplate () {
		return isInTemplate;
	}

	public MacroDefinitionAttribute getTemplateDefinedParameterDefinition() {
		return templateDefinedParameterDefinition;
	}

	public void setInMacroDefinition(boolean isInMacroDefinition) {
		this.isInMacroDefinition = isInMacroDefinition;
	}

	public List getMacroDefinitionAttributes(ExtendedContext context) {
		if (isTemplateDefinedParameterInMacroDefinition()) {
			// find the associated macro in the parsing session stack
			List elements = new ArrayList();
			context.addToElementScope(elements);
			for (int i = 0; i < elements.size(); i++) {
				Element e = (Element) elements.get(i);
				if (e instanceof MacroDirective) {
					MacroDirective md = (MacroDirective) e;
					// make sure we've got a match
					MacroDefinitionAttribute attr = md.getAttribute(getName());
					if (null != attr && attr.isTemplateDefinedParameter()) {
						return attr.getTDPAttributes();
					}
				}
			}
			throw new ExecutionException("Unknown template=defined parameter '"
					+ getName() + "'", this);
		} else {
			if (null != templateDefinedParameterDefinition) {
				return templateDefinedParameterDefinition.getTDPAttributes();
			} else if (null != baseMacroInstance
					&& null != baseMacroInstance.getMacroDefinition())
				return baseMacroInstance.getMacroDefinition().getAttributes();
			else
				return null;
		}
	}

	public boolean isTemplateDefinedParameterInMacroDefinition() {
		return isTemplateDefinedParameterInMacroDefinition;
	}

	public MacroHeaderElement getHeader() {
		return null;
	}

	public MacroFooterElement getFooter() {
		return null;
	}

	public boolean generatesOutput() {
		return false;
	}

	public boolean isBodyEmpty() {
		return isBodyEmpty;
	}
}