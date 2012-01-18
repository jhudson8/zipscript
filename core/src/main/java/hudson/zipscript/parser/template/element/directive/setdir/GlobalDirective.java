/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.directive.setdir;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ElementIndex;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.NonOutputElement;
import hudson.zipscript.parser.template.element.directive.AbstractDirective;
import hudson.zipscript.parser.template.element.lang.AssignmentElement;
import hudson.zipscript.parser.template.element.special.SpecialStringElement;

import java.io.Writer;
import java.util.List;

public class GlobalDirective extends AbstractDirective implements
		NonOutputElement {

	private String varName;
	private Element setElement;

	public GlobalDirective(String contents, ParsingSession session,
			int contentPosition) throws ParseException {
		parseContents(contents, session, contentPosition);
	}

	private void parseContents(String contents, ParsingSession session,
			int contentPosition) throws ParseException {
		java.util.List elements = parseElements(contents, session,
				contentPosition);
		try {
			if (elements.get(0) instanceof SpecialStringElement) {
				this.varName = ((SpecialStringElement) elements.remove(0))
						.getTokenValue();
			} else {
				throw new ParseException(this,
						"Invalid sequence.  Expecting variable name");
			}
			if (!(elements.remove(0) instanceof AssignmentElement))
				throw new ParseException(this,
						"Invalid sequence.  Expecting '='");
			if (elements.size() > 1)
				throw new ParseException(this,
						"Invalid sequence.  Improperly formed set expression");
			else
				this.setElement = (Element) elements.get(0);
		} catch (IndexOutOfBoundsException e) {
			throw new ParseException(this,
					"Improperly formed set expression: must have at least 3 tokens");
		}
	}

	public void merge(ExtendedContext context, Writer sw)
			throws ExecutionException {
		context.getRootContext().put(varName, setElement.objectValue(context));
	}

	public ElementIndex normalize(int index, List elementList,
			ParsingSession session) throws ParseException {
		return null;
	}

	public List getChildren() {
		return null;
	}

	public boolean generatesOutput() {
		return false;
	}
}