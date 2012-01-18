/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.context;

import hudson.zipscript.ResourceContainer;
import hudson.zipscript.parser.Constants;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.directive.macrodir.MacroDirective;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class AbstractContext implements ExtendedContext {

	private Locale locale;
	private ParsingSession parsingSession;
	private ResourceContainer resourceContainer;
	private Map importDefinitions;
	private Map initializeMap;
	private boolean refreshTemplates = true;

	public boolean doRefreshTemplates() {
		return refreshTemplates;
	}

	public ParsingSession getParsingSession() {
		return parsingSession;
	}

	public void setParsingSession(ParsingSession parsingSession) {
		this.parsingSession = parsingSession;
		this.refreshTemplates = parsingSession.getParameters()
				.getPropertyAsBoolean(Constants.REFRESH_TEMPLATES, true);
	}

	public ResourceContainer getResourceContainer() {
		return resourceContainer;
	}

	public void setResourceContainer(ResourceContainer resourceContainer) {
		this.resourceContainer = resourceContainer;
	}

	public boolean isInitialized() {
		return (null != initializeMap && initializeMap.size() > 0);
	}

	public boolean isInitialized(Element topLevelElement) {
		if (null == initializeMap)
			return false;
		else
			return (null != initializeMap.get(topLevelElement));
	}

	public void markInitialized(Element topLevelElement) {
		if (null == initializeMap)
			initializeMap = new HashMap();
		initializeMap.put(topLevelElement, Boolean.TRUE);
	}

	public MacroDirective getMacro(String name) {
		return getParsingSession().getMacro(name);
	}

	public String getMacroImportPath(String namespace) {
		if (null == importDefinitions)
			return null;
		else
			return (String) importDefinitions.get(namespace);
	}

	public void addMacroImport(String namespace, String macroPath) {
		if (null == importDefinitions)
			importDefinitions = new HashMap();
		importDefinitions.put(namespace, macroPath);
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public ExtendedContext getRootContext() {
		return this;
	}

	public void appendMacroNestedAttributes(Map m) {
		// if we are using this context we are at the top level
		// and not in a macro definition
	}

	public void addToElementScope(List nestingStack) {
		// this is a root element
	}
}