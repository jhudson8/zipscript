/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.resource.macrolib;

import hudson.zipscript.parser.template.element.directive.macrodir.MacroDirective;

public interface MacroProvider {

	public MacroDirective getMacro(String name);

	public String getMacroImportPath(String namespace);
}
