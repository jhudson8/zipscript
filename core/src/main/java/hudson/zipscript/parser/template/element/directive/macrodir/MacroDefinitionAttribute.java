/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.directive.macrodir;

import hudson.zipscript.parser.template.element.Element;

import java.util.List;

public class MacroDefinitionAttribute {

	private String name;
	private Element defaultValue;
	private boolean isRequired;
	private boolean isTemplateDefinedParameter;
	private List tdpAttributes;

	public MacroDefinitionAttribute(String name, Element defaultValue,
			boolean isRequired) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.isRequired = isRequired;
	}

	public MacroDefinitionAttribute(String name, List tdpAttributes,
			boolean isRequired) {
		this.name = name;
		this.tdpAttributes = tdpAttributes;
		this.isRequired = isRequired;
		this.isTemplateDefinedParameter = true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Element getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(Element defaultValue) {
		this.defaultValue = defaultValue;
	}

	public boolean isRequired() {
		return isRequired;
	}

	public void setRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}

	public String toString() {
		return "macro attribute: " + name + " = " + getDefaultValue();
	}

	public boolean isTemplateDefinedParameter() {
		return isTemplateDefinedParameter;
	}

	public void setTemplateDefinedParameter(boolean isTemplateDefinedParameter) {
		this.isTemplateDefinedParameter = isTemplateDefinedParameter;
	}

	public List getTDPAttributes() {
		return tdpAttributes;
	}
}