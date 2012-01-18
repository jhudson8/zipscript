/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.directive.macrodir;

import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ElementIndex;
import hudson.zipscript.parser.template.data.ParsingSession;

import java.util.List;

public class MacroFooterElement extends MacroHeaderElement {

	public MacroFooterElement(String contents, ParsingSession session,
			int position) throws ParseException {
		super(contents, session, position);
	}

	public ElementIndex normalize(int index, List elementList,
			ParsingSession session) throws ParseException {
		// set element in macro
		((MacroInstanceDirective) session.getNestingStack().get(
				session.getNestingStack().size() - 1)).setFooter(this);
		return new ElementIndex(null, -1);
	}
}