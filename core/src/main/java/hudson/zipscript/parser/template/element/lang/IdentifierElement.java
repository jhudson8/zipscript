/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ElementIndex;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.AbstractElement;

import java.io.Writer;
import java.util.List;

public class IdentifierElement extends AbstractElement {

	public void merge(ExtendedContext context, Writer sw) {
	}

	public boolean booleanValue(ExtendedContext context)
			throws ExecutionException {
		throw new ExecutionException(
				"identifiers can not be evaluated as booleans", this);
	}

	public Object objectValue(ExtendedContext context)
			throws ExecutionException {
		throw new ExecutionException(
				"identifiers can not be evaluated as objects", this);
	}

	public ElementIndex normalize(int index, List elementList,
			ParsingSession session) throws ParseException {
		return null;
	}

	public String getTokenValue() {
		return toString();
	}

	public List getChildren() {
		return null;
	}
}