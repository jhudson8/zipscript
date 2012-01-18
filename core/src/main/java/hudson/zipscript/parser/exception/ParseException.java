/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.exception;

import hudson.zipscript.parser.template.data.LinePosition;
import hudson.zipscript.parser.template.data.ParsingResult;
import hudson.zipscript.parser.template.element.Element;

public class ParseException extends Exception {
	private static final long serialVersionUID = -1363961609103512907L;

	private long absolutePosition;
	private int length;
	private ParsingResult parseData;
	private Element element;
	private String resource;

	public ParseException(long position, String message) {
		this(null, position, Integer.MIN_VALUE, message);
	}

	public ParseException(long position, int length, String message) {
		this(null, position, length, message);
	}

	public ParseException(Element element, String message) {
		this(element, Long.MIN_VALUE, Integer.MIN_VALUE, message);
	}

	public ParseException(Element element, long position, int length,
			String message) {
		super(message);
		if (position >= 0)
			this.absolutePosition = position;
		else if (null != element)
			this.absolutePosition = element.getElementPosition();
		if (length > 0)
			this.length = length;
		else if (null != this.element)
			this.length = element.getElementLength();
		this.element = element;
		if (null != element)
			this.length = element.getElementLength();
	}

	public void setParsingResult(ParsingResult parsingData) {
		this.parseData = parsingData;
	}

	public String getMessage() {
		StringBuffer sb = new StringBuffer();
		if (null != resource)
			sb.append("[" + resource + "] ");
		if (null != parseData) {
			LinePosition lp = parseData.getLinePosition(absolutePosition);
			sb.append("(line " + lp.line + ", position " + lp.position + ") ");
		}
		sb.append(super.getMessage());
		return sb.toString();
	}

	public int getLine() {
		if (null == parseData)
			return 0;
		return parseData.getLinePosition(absolutePosition).line;
	}

	public int getPosition() {
		if (null == parseData)
			return 0;
		return parseData.getLinePosition(absolutePosition).position;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public int getLength() {
		return length;
	}
}