/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript;

import hudson.zipscript.parser.template.element.component.Component;
import hudson.zipscript.parser.template.element.lang.variable.adapter.VariableAdapterFactory;
import hudson.zipscript.parser.util.ClassUtil;
import hudson.zipscript.parser.util.translator.GoogleTranslator;
import hudson.zipscript.parser.util.translator.Translator;
import hudson.zipscript.plugin.Plugin;
import hudson.zipscript.resource.ClasspathResourceLoader;
import hudson.zipscript.resource.ResourceLoader;
import hudson.zipscript.resource.StringResourceLoader;
import hudson.zipscript.resource.macrolib.MacroManager;

import java.util.Map;

public class ResourceContainer {

	private ZipEngine zipEngine;
	private Plugin[] plugins;
	private MacroManager macroManager;
	private Component[] components;
	private VariableAdapterFactory variableAdapterFactory;
	private Map initParameters;
	private static Translator translator;

	private ResourceLoader templateResourceLoader = new ClasspathResourceLoader();
	private ResourceLoader includeResourceLoader = null;
	private ResourceLoader macroLibResourceLoader = null;
	private ResourceLoader evalResourceLoader = new StringResourceLoader();

	public ResourceContainer(ZipEngine zipEngine, Plugin[] plugins,
			MacroManager macroManager,
			VariableAdapterFactory variableAdapterFactory,
			ResourceLoader templateResourceLoader,
			ResourceLoader includeResourceLoader,
			ResourceLoader macroLibResourceLoader,
			ResourceLoader evalResourceLoader, Component[] components,
			Map initParameters) {
		this.zipEngine = zipEngine;
		this.plugins = plugins;
		this.macroManager = macroManager;
		this.variableAdapterFactory = variableAdapterFactory;

		this.templateResourceLoader = templateResourceLoader;
		this.includeResourceLoader = includeResourceLoader;
		this.macroLibResourceLoader = macroLibResourceLoader;
		this.evalResourceLoader = evalResourceLoader;

		this.components = components;
		this.initParameters = initParameters;
		macroManager.setResourceContainer(this);
	}

	public MacroManager getMacroManager() {
		return macroManager;
	}

	public Component[] getComponents() {
		return components;
	}

	public VariableAdapterFactory getVariableAdapterFactory() {
		return variableAdapterFactory;
	}

	public Map getInitParameters() {
		return initParameters;
	}

	public void setMacroManager(MacroManager macroManager) {
		this.macroManager = macroManager;
	}

	public void setComponents(Component[] components) {
		this.components = components;
	}

	public void setVariableAdapterFactory(
			VariableAdapterFactory variableAdapterFactory) {
		this.variableAdapterFactory = variableAdapterFactory;
	}

	public void setInitParameters(Map initParameters) {
		this.initParameters = initParameters;
	}

	public ResourceLoader getTemplateResourceLoader() {
		return templateResourceLoader;
	}

	public ResourceLoader getMacroLibResourceLoader() {
		if (null == macroLibResourceLoader)
			return getTemplateResourceLoader();
		else
			return macroLibResourceLoader;
	}

	public ResourceLoader getEvalResourceLoader() {
		return evalResourceLoader;
	}

	public ResourceLoader getIncludeResourceLoader() {
		if (null == includeResourceLoader)
			return getTemplateResourceLoader();
		else
			return includeResourceLoader;
	}

	public Plugin[] getPlugins() {
		return plugins;
	}

	public ZipEngine getEngine() {
		return zipEngine;
	}

	public void setTemplateResourceLoader(ResourceLoader templateResourceLoader) {
		this.templateResourceLoader = templateResourceLoader;
	}

	public void setIncludeResourceLoader(ResourceLoader includeResourceLoader) {
		this.includeResourceLoader = includeResourceLoader;
	}

	public void setMacroLibResourceLoader(ResourceLoader macroLibResourceLoader) {
		this.macroLibResourceLoader = macroLibResourceLoader;
	}

	public void setEvalResourceLoader(ResourceLoader evalResourceLoader) {
		this.evalResourceLoader = evalResourceLoader;
	}

	public Translator getTranslator() {
		synchronized (this) {
			if (null == translator) {
				translator = (Translator) ClassUtil.loadResource("translator",
						initParameters, Translator.class,
						GoogleTranslator.class, null);
			}
		}
		return translator;
	}
}