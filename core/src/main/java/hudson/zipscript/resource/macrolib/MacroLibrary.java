/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.resource.macrolib;

import hudson.zipscript.parser.template.element.directive.macrodir.MacroDirective;
import hudson.zipscript.resource.Resource;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MacroLibrary implements MacroProvider {

	private String namespace;
	private Resource resource;
	private Map macroDefinitions = new HashMap();

	public MacroLibrary(String namespace, Resource resource) {
		this.namespace = namespace;
		this.resource = resource;
	}

	public boolean hasBeenModified() {
		return resource.hasBeenModified();
	}

	public Set getMacroNames() {
		return macroDefinitions.keySet();
	}

	public void addMacroDefinition(MacroDirective macro) {
		macro.setMacroLibrary(this);
		macroDefinitions.put(macro.getName(), macro);
	}

	public MacroDirective getMacro(String name) {
		return (MacroDirective) macroDefinitions.get(name);
	}

	public String getNamespace() {
		return namespace;
	}

	public String getMacroImportPath(String namespace) {
		return null;
	}

	public Resource getResource() {
		return resource;
	}
}