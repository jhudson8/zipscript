/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element;

public class ElementAttribute {

	private String name;
	private Element value;

	public ElementAttribute(String name, Element value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Element getValue() {
		return value;
	}

	public void setValue(Element value) {
		this.value = value;
	}
}