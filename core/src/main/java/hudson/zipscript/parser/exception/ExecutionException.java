/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.exception;

import hudson.zipscript.parser.template.data.LinePosition;
import hudson.zipscript.parser.template.data.ParsingResult;
import hudson.zipscript.parser.template.element.Element;

public class ExecutionException extends RuntimeException {
	private static final long serialVersionUID = -1363961609103512907L;

	private Element element;
	ParsingResult parsingResult;
	private String resource;

	public ExecutionException(String message, Element element) {
		super(message);
		this.element = element;
	}

	public ExecutionException(String message, Element element,
			Exception thrownException) {
		super(message, thrownException);
		this.element = element;
	}

	public void setParsingResult(ParsingResult parsingResult) {
		this.parsingResult = parsingResult;
	}

	public String getMessage() {
		StringBuffer sb = new StringBuffer();
		if (null != resource)
			sb.append("[" + resource + "] ");
		if (null != element && null != parsingResult) {
			LinePosition lp = parsingResult.getLinePosition(element
					.getElementPosition());
			sb.append("(line " + lp.line + ", position " + lp.position + ") ");
		}
		sb.append(super.getMessage());
		return sb.toString();
	}

	public Element getElement() {
		return element;
	}

	public void setElement(Element element) {
		this.element = element;
	}

	public int getLine() {
		if (null == parsingResult)
			return 0;
		return parsingResult.getLinePosition(element.getElementPosition()).line;
	}

	public int getPosition() {
		if (null == parsingResult)
			return 0;
		return parsingResult.getLinePosition(element.getElementPosition()).position;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}
}