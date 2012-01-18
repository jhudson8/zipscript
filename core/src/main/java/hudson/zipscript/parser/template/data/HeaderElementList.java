/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.data;

import hudson.zipscript.parser.template.element.Element;

import java.util.List;

public class HeaderElementList {

	private Element header;
	private List children;

	public HeaderElementList(Element header, List children) {
		this.header = header;
		this.children = children;
	}

	/**
	 * @return the header
	 */
	public Element getHeader() {
		return header;
	}

	/**
	 * @param header
	 *            the header to set
	 */
	public void setHeader(Element header) {
		this.header = header;
	}

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
}
