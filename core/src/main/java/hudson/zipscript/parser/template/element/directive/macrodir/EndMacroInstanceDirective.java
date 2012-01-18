/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.directive.macrodir;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ElementIndex;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.directive.NestableDirective;

import java.io.Writer;
import java.util.List;

public class EndMacroInstanceDirective extends NestableDirective {

	private String name;
	private boolean isTemplateDefinedParameterInMacroDefinition;

	public EndMacroInstanceDirective(String name) {
		this(name, false);
	}

	public EndMacroInstanceDirective(String name,
			boolean isTemplateDefinedParameterInMacroDefinition) {
		this.name = name;
		this.isTemplateDefinedParameterInMacroDefinition = isTemplateDefinedParameterInMacroDefinition;
	}

	public String toString() {
		return "[/@" + getName() + "]";
	}

	public void merge(ExtendedContext context, Writer sw)
			throws ExecutionException {
		throw new ExecutionException("Invalid macro directive", this);
	}

	public ElementIndex normalize(int index, List elementList,
			ParsingSession session) throws ParseException {
		return null;
	}

	public String getName() {
		return name;
	}

	public boolean isTemplateDefinedParameterInMacroDefinition() {
		return isTemplateDefinedParameterInMacroDefinition;
	}
}
