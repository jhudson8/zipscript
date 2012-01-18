/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.plugin.struts2;

import hudson.zipscript.ZipEngine;
import hudson.zipscript.ext.data.RequestParameterMap;
import hudson.zipscript.parser.Constants;
import hudson.zipscript.parser.context.Context;
import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.InitializationException;
import hudson.zipscript.parser.template.element.component.Component;
import hudson.zipscript.parser.template.element.lang.variable.adapter.VariableAdapterFactory;
import hudson.zipscript.plugin.Plugin;
import hudson.zipscript.resource.WebInfResourceLoader;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.TextProviderFactory;

public class Struts2Plugin implements Plugin {
	
	public Component[] getComponents() {
		return null;
	}

	public VariableAdapterFactory getVariableAdapterFactory() {
		return new hudson.zipscript.plugin.struts2.VariableAdapterFactory();
	}

	public void initialize(ZipEngine zipEngine, Map initParameters)
			throws InitializationException {
		if (null == initParameters.get(Constants.TEMPLATE_RESOURCE_LOADER_CLASS)) {
			initParameters.put(Constants.TEMPLATE_RESOURCE_LOADER_CLASS,
					WebInfResourceLoader.class.getName());
			initParameters.put("templateResourceLoader.pathPrefix", "zs/");
		}
		if (null == initParameters.get(Constants.INCLUDE_RESOURCE_LOADER_CLASS)) {
			initParameters.put(Constants.INCLUDE_RESOURCE_LOADER_CLASS,
					WebInfResourceLoader.class.getName());
			initParameters.put("includeResourceLoader.pathPrefix", "zs/fragments/");
		}
		if (null == initParameters.get(Constants.MACROLIB_RESOURCE_LOADER_CLASS)) {
			initParameters.put(Constants.MACROLIB_RESOURCE_LOADER_CLASS,
					WebInfResourceLoader.class.getName());
			initParameters.put("macroLibResourceLoader.pathPrefix", "zs/macros/");
		}
	}

	public void initialize(ExtendedContext context)
			throws InitializationException {
		ActionContext actionContext = ActionContext.getContext();
		if (null != actionContext) {
			Object action = actionContext.getActionInvocation().getAction();
			if (action instanceof LocaleProvider) {
				context.put(Constants.RESOURCE, new TextProviderFactory().createInstance(
						action.getClass(), (LocaleProvider) action));
				context.setLocale(((LocaleProvider) action).getLocale());
			}
			HttpServletRequest req = ServletActionContext.getRequest();
			context.put(hudson.zipscript.plugin.struts2.Constants.REQUEST, req);
			context.put(hudson.zipscript.plugin.struts2.Constants.SESSION, req.getSession());
			context.put(hudson.zipscript.plugin.struts2.Constants.RESPONSE, ServletActionContext.getResponse());
			context.put(hudson.zipscript.plugin.struts2.Constants.ACTION, action);
			context.put(hudson.zipscript.plugin.struts2.Constants.PARAMS, new RequestParameterMap(req));
		}
	}

	public Context wrapContextObject(Object object) {
		return null;
	}
}