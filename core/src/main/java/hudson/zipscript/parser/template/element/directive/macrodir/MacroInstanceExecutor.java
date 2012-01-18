/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.directive.macrodir;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.context.MacroInstanceEntityContext;
import hudson.zipscript.parser.template.element.NoAutoEscapeElement;
import hudson.zipscript.parser.template.element.ToStringWithContextElement;

import java.io.Writer;
import java.util.List;

public class MacroInstanceExecutor implements ToStringWithContextElement,
		NoAutoEscapeElement {

	private MacroOrientedElement macroInstance;
	private ExtendedContext bodyContext;

	public MacroInstanceExecutor(
			MacroOrientedElement macroInstance, ExtendedContext bodyContext) {
		this.macroInstance = macroInstance;
			this.bodyContext = bodyContext;
	}

	public void setBodyContext (ExtendedContext bodyContext) {
		this.bodyContext = bodyContext;
	}

	public List getChildren() {
		return macroInstance.getChildren();
	}

	public String toString(ExtendedContext context) {
		if (context instanceof MacroInstanceEntityContext)
			((MacroInstanceEntityContext) context).setPostMacroContext(context);
		return macroInstance.getNestedContent(context);
	}

	public void append(ExtendedContext context, Writer writer) {
		macroInstance.writeNestedContent(this.bodyContext, writer);
	}

	public MacroOrientedElement getMacroInstance() {
		return macroInstance;
	}

	public void put (String key, Object value) {
		bodyContext.put(key, value);
	}
}