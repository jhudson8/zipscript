/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.directive.breakdir;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ElementIndex;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.directive.AbstractDirective;

import java.io.Writer;
import java.util.List;

public class BreakDirective extends AbstractDirective {

	private int contentStartPosition;

	public BreakDirective(int contentStartPosition) throws ParseException {
		this.contentStartPosition = contentStartPosition;
	}

	public void validate(ParsingSession session) throws ParseException {
		// we must be within a breakable directive
		boolean isValid = false;
		for (int i = session.getNestingStack().size() - 1; i >= 0; i--) {
			if (session.getNestingStack().get(i) instanceof BreakableDirective) {
				isValid = true;
				break;
			}
		}
		if (!isValid)
			throw new ParseException(this.contentStartPosition,
					"[#break/] directives can only used within a breakable directive");
	}

	public void merge(ExtendedContext context, Writer sw)
			throws ExecutionException {
		throw new BreakException();
	}

	public ElementIndex normalize(int index, List elementList,
			ParsingSession session) throws ParseException {
		return null;
	}

	public List getChildren() {
		return null;
	}

	public String toString() {
		return "[#break/]";
	}
}