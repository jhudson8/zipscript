/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.directive.macrodir;

import hudson.zipscript.parser.Constants;
import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.context.MacroInstanceEntityContext;
import hudson.zipscript.parser.context.NestedContextWrapper;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ElementIndex;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.DebugElementContainerElement;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.ElementAttribute;
import hudson.zipscript.parser.template.element.NestableElement;
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

public class MacroInstanceDirective extends NestableElement implements
		MacroInstanceAware, DebugElementContainerElement, MacroOrientedElement {

	private boolean isOrdinal = true;
	private String contents;
	private String name;
	private String namespace;

	private List attributes = new ArrayList();
	private Map attributeMap;
	private MacroDirective macro;
	private MacroDirective baseMacroDefinition;
	private boolean isInMacroDefinition;
	private boolean isInTemplate;

	private MacroHeaderElement header;
	private MacroFooterElement footer;
	private boolean isBodyEmpty;

	// text normalization
	TextElement previousTextElement;

	// private ParsingSession parsingSession;
	private int contentPosition;

	// debug
	private List internalElements;

	public MacroInstanceDirective(String contents, boolean isFlat,
			ParsingSession parsingSession, int contentPosition)
			throws ParseException {
		this.contents = contents;
		setFlat(isFlat);
		this.contentPosition = contentPosition;
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
			throw new ParseException(this, "Macro name was not specified");
		Element e;
		e = (Element) elements.remove(0);
		if (e instanceof SpecialStringElement)
			name = ((SpecialStringElement) e).getTokenValue();
		// validate name
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (!(Character.isLetterOrDigit(c) || c == '_' || c == '-' || c == Constants.NAMESPACE_SEPARATOR))
				throw new ParseException(contentPosition,
						"Invalid macro name '" + name + "'");
		}
		int index = name.indexOf(Constants.NAMESPACE_SEPARATOR);
		if (index > 0) {
			String s = name;
			namespace = s.substring(0, index);
			name = s.substring(index + 1, s.length());
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
			if (e instanceof MacroInstanceDirective) {
				MacroInstanceDirective mid = (MacroInstanceDirective) e;
				baseMacroDefinition = session.getResourceContainer()
						.getMacroManager().getMacro(mid.getName(),
								mid.getNamespace(), session);
				isInMacroDefinition = true;
				break;
			} else if (e instanceof MacroDirective) {
				isInMacroDefinition = true;
				break;
			}
		}
		if (session.getParsingContext() == ParsingSession.PARSING_CONTEXT_TEMPLATE)
			this.isInTemplate = true;

		// find macro in session
		// if we're in the header/footer - we can't validate until the whole
		// template is parsed - how to do that?
		macro = session.getResourceContainer().getMacroManager().getMacro(
				getName(), getNamespace(), session);

		if (null == macro) {
			if (null != namespace) {
				// if dynamically defined import - lazy load
				if (session.isDynamicMacroImport(namespace)) {
					// lazy load
				} else {
					throw new ParseException(this, "Undefined macro name '"
							+ getName() + "'");
				}
			} else {
				throw new ParseException(this, "Undefined macro name '"
						+ getName() + "'");
			}
		}

		if (!isOrdinal && null != macro) {
			// make sure all attributes are defined
			for (int i = 0; i < attributes.size(); i++) {
				ElementAttribute attribute = (ElementAttribute) attributes
						.get(i);
				if (null == macro.getAttribute(attribute.getName())) {
					throw new ParseException(contentPosition,
							"Undefined macro attribute '" + attribute.getName()
									+ "'");
				}
			}
			// make sure we're passing all non-required attributes
			for (int i = 0; i < macro.getAttributes().size(); i++) {
				MacroDefinitionAttribute attribute = (MacroDefinitionAttribute) macro
						.getAttributes().get(i);
				if (attribute.isTemplateDefinedParameter()) {
					validateTemplateAttribute(attribute, this);
				} else if (attribute.isRequired()
						&& null == attribute.getDefaultValue()
						&& null == getAttribute(attribute.getName())) {
					// it's required
					throw new ParseException(contentPosition,
							"Undefined required macro attriute '"
									+ attribute.getName() + "'");
				}
			}
		}

		if (null != header)
			header.validate(session);
		if (null != footer)
			footer.validate(session);

		if (null != getChildren()
				&& PropertyUtil.getProperty(Constants.TRIM_MACRO_BODY, true,
						session.getParameters().getInitParameters())) {
			// trim the body
			this.isBodyEmpty = StringUtil.trim(getChildren());
		}
	}

	protected void validateTemplateAttribute(
			MacroDefinitionAttribute attribute, MacroOrientedElement mid)
			throws ParseException {
		if (attribute.isTemplateDefinedParameter()) {
			// make sure we have the parameter
			List templateDefinedParameters = new ArrayList();
			loadTemplateDefinedAttributes(attribute.getName(), mid,
					templateDefinedParameters);
			if (templateDefinedParameters.size() == 0 && attribute.isRequired())
				throw new ParseException(mid,
						"Missing template defined parameter '"
								+ attribute.getName() + "'");
			for (Iterator i = templateDefinedParameters.iterator(); i.hasNext();) {
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
		return (e instanceof MacroInstanceDirective && ((MacroInstanceDirective) e)
				.getName().equals(getName()));
	}

	protected boolean isEndElement(Element e) {
		if (e instanceof EndMacroInstanceDirective) {
			EndMacroInstanceDirective eid = (EndMacroInstanceDirective) e;
			if (eid.getName().equals(getFullName()))
				return true;
		}
		return false;
	}

	protected boolean allowSelfNesting() {
		return false;
	}

	public String toString() {
		return "[@" + contents + "]";
	}

	public void merge(ExtendedContext context, Writer sw)
			throws ExecutionException {
		if (null == macro) {
			// we might need to lazy load
			macro = context.getResourceContainer().getMacroManager().getMacro(
					getName(), getNamespace(), context);
		} else if (context.doRefreshTemplates() && null != namespace
				&& macro.getMacroLibrary().hasBeenModified()) {
			// reload the macro
			try {
				macro = context.getResourceContainer().getMacroManager()
						.reloadMacro(getName(), getNamespace(), context);
			}
			catch (ParseException e) {
				throw new ExecutionException(e.getMessage(), null, e);
			}
		}
		if (null == macro) {
			throw new ExecutionException("Undefined macro '" + getName() + "'",
					this);
		}

		ExtendedContext bodyContext = context;
//		if (isInTemplate()) {
//			bodyContext = context.getTemplateContext();
//		}
		macro.executeMacro(context, isOrdinal(), getAttributes(),
					new MacroInstanceExecutor(this, bodyContext), sw);
	}

	public String getNestedContent(ExtendedContext context)
			throws ExecutionException {
		StringWriter sw = new StringWriter();
		for (Iterator i = getChildren().iterator(); i.hasNext();) {
			((Element) i.next()).merge(context, sw);
		}
		return sw.toString();
	}

	public void writeNestedContent(ExtendedContext context, Writer writer)
			throws ExecutionException {
		if (null != getChildren()) {
			for (Iterator i = getChildren().iterator(); i.hasNext();) {
				((Element) i.next()).merge(context, writer);
			}
		}
	}

	public MacroDirective getMacroDefinition() {
		return macro;
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
		super.appendTemplateDefinedParameters(getChildren(), context, list,
				macro, additionalContextEntries);
	}

	public boolean isInMacroDefinition() {
		return isInMacroDefinition;
	}

	public boolean isInTemplate() {
		return isInTemplate;
	}

	public String getNamespace() {
		return namespace;
	}

	public MacroDirective getBaseMacroDefinition() {
		return baseMacroDefinition;
	}

	public void setInMacroDefinition(boolean isInMacroDefinition) {
		this.isInMacroDefinition = isInMacroDefinition;
	}

	public String getFullName() {
		if (null == namespace)
			return name;
		else
			return namespace + Constants.NAMESPACE_SEPARATOR + name;
	}

	public List getMacroDefinitionAttributes(ExtendedContext context) {
		if (null != macro) {
			return macro.getAttributes();
		} else
			return baseMacroDefinition.getAttributes();
	}

	public MacroHeaderElement getHeader() {
		return header;
	}

	public void setHeader(MacroHeaderElement header) {
		this.header = header;
	}

	public MacroFooterElement getFooter() {
		return footer;
	}

	public void setFooter(MacroFooterElement footer) {
		this.footer = footer;
	}

	public boolean generatesOutput() {
		return true;
	}

	public boolean isBodyEmpty() {
		return isBodyEmpty;
	}
}