/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package org.apache.struts2.views.zipscript;

import hudson.zipscript.ZipEngine;
import hudson.zipscript.ext.data.ResultData;
import hudson.zipscript.parser.Constants;
import hudson.zipscript.parser.context.Context;
import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.util.StringUtil;
import hudson.zipscript.plugin.Plugin;
import hudson.zipscript.plugin.struts2.Struts2Plugin;
import hudson.zipscript.plugin.struts2.parser.context.ActionRequestContextWrapper;
import hudson.zipscript.resource.WebInfResourceLoader;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.StrutsConstants;
import org.apache.struts2.dispatcher.Dispatcher;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.location.LocatableProperties;

public class ZipScriptManager {

	private static final String INIT_PARAM_PREFIX = "zipscript.";
	private static final String CONFIG_SERVLET_CONTEXT_KEY = ZipEngine.class.getName();

	private String encoding;
	private Map<String, String> contextValues;
	
	public ZipScriptManager () {
		super();
	}

	@Inject(StrutsConstants.STRUTS_I18N_ENCODING)
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getEncoding() {
		return encoding;
	}

	public final synchronized ZipEngine getZipEngine (ServletContext servletContext) {
		ZipEngine zipEngine = (ZipEngine) servletContext.getAttribute(CONFIG_SERVLET_CONTEXT_KEY);
		if (null == zipEngine) {
			zipEngine = createNewZipEngine(servletContext);
			servletContext.setAttribute(CONFIG_SERVLET_CONTEXT_KEY, zipEngine);
		}
		return zipEngine;
	}

	protected ZipEngine createNewZipEngine (ServletContext servletContext) {
		Map<String, Object> props = new HashMap<String, Object>();

		// is there a better way to do this?
		LocatableProperties locatableProperties = new LocatableProperties();
		List<ConfigurationProvider> configurationProviders = Dispatcher.getInstance()
				.getConfigurationManager().getConfigurationProviders();
		for (ConfigurationProvider cp : configurationProviders) {
			try {
				cp.register(null, locatableProperties);
			}
			catch (Exception e) {}
		}
		for (Enumeration<Object> e=locatableProperties.keys(); e.hasMoreElements(); ) {
			String key = e.nextElement().toString();
			if (key.startsWith(INIT_PARAM_PREFIX)) {
				props.put(key.substring(INIT_PARAM_PREFIX.length()), locatableProperties.get(key));
			}
		}

		String contextValues = (String) props.get("contextValues");
		if (null != contextValues) {
			this.contextValues = StringUtil.getProperties(contextValues);
		}
		
		props.put(Constants.INCLUDE_RESOURCE_LOADER_PARAMETER, servletContext);
		props.put(Constants.MACROLIB_RESOURCE_LOADER_PARAMETER, servletContext);

		ZipEngine zipEngine = ZipEngine.createInstance(
				props, new Plugin[]{new Struts2Plugin()});

		String autoImports = (String) props.get("autoImport");
		if (null != autoImports) {
			StringTokenizer st = new StringTokenizer(autoImports, ";");
			while (st.hasMoreElements()) {
				String name = st.nextToken().trim();
				if (name.length() > 0) {
					String namespace = name.substring(0, name.indexOf('.'));
					try {
						zipEngine.addMacroLibrary(namespace, name);
					}
					catch (ParseException e) {
						throw new ExecutionException(e.getMessage(), null, e);
					}
				}
			}
		}

		// set resource loaders
		String rootDirectory = getProperty("rootDirectory", "zs/", props);
		String includesDirectory = getProperty("includesDirectory", "includes/", props);
		String macrosDirectory = getProperty("macrosDirectory", "macros/", props);

		zipEngine.setTemplateResourceLoader(
				new WebInfResourceLoader(rootDirectory));
		zipEngine.setIncludeResourceLoader(
				new WebInfResourceLoader(rootDirectory + includesDirectory));
		zipEngine.setMacroLibResourceLoader(
				new WebInfResourceLoader(rootDirectory + macrosDirectory));

		return zipEngine;
	}

	private String getProperty (String key, String defaultValue, Map properties) {
		Object obj = properties.get(key);
		if (null == obj) return defaultValue;
		else return obj.toString();
	}

	public Context createContext (
			ActionInvocation actionInvocation, ResultData resultData, HttpServletRequest request) {
		ExtendedContext ctx = new ActionRequestContextWrapper(
				actionInvocation.getAction(), request);
		if (null != resultData)
			ctx.put(hudson.zipscript.plugin.struts2.Constants.LAYOUT, resultData);
		if (null != contextValues) {
			for (Map.Entry<String, String> entry : contextValues.entrySet()) {
				ctx.put(entry.getKey(), entry.getValue());
			}
		}
		return ctx;
	}
}
