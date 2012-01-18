/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.comment;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ElementIndex;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.NonOutputElement;

import java.io.Writer;
import java.util.List;

public class CommentElement implements Element, NonOutputElement {

	private static CommentElement instance = new CommentElement();

	public static CommentElement getInstance() {
		return instance;
	}

	public boolean booleanValue(ExtendedContext context)
			throws ExecutionException {
		return false;
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

	public void merge(ExtendedContext context, Writer sw)
			throws ExecutionException {
	}

	public Object objectValue(ExtendedContext context)
			throws ExecutionException {
		return null;
	}

	public void setElementLength(int length) {
	}

	public void setElementPosition(long position) {
	}

	public List getChildren() {
		return null;
	}

	public void validate(ParsingSession session) throws ParseException {
	}

	public boolean generatesOutput() {
		return false;
	}
}