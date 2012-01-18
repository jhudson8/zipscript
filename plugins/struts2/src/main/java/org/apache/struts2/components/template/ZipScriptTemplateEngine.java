/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package org.apache.struts2.components.template;

import hudson.zipscript.ZipEngine;
import hudson.zipscript.parser.exception.ResourceNotFoundException;
import hudson.zipscript.template.Template;

import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.views.zipscript.ZipScriptManager;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * ZipScript based template engine.
 */
public class ZipScriptTemplateEngine extends BaseTemplateEngine {

	private static final Log LOG = LogFactory
			.getLog(ZipScriptTemplateEngine.class);
	private static final Map<String, String> templatePaths = new HashMap<String, String>();

	private ZipScriptManager zipScriptManager;

	@Inject
	public void setZipScriptManager(ZipScriptManager mgr) {
		this.zipScriptManager = mgr;
	}

	public void renderTemplate(TemplateRenderingContext templateContext)
			throws Exception {
		// get the various items required from the stack
		ValueStack stack = templateContext.getStack();
		Map valueContext = stack.getContext();
		ServletContext servletContext = (ServletContext) valueContext
				.get(ServletActionContext.SERVLET_CONTEXT);
		HttpServletRequest req = (HttpServletRequest) valueContext
				.get(ServletActionContext.HTTP_REQUEST);
		HttpServletResponse res = (HttpServletResponse) valueContext
				.get(ServletActionContext.HTTP_RESPONSE);
		ActionInvocation actionInvocation = ActionContext.getContext().getActionInvocation();

		ZipEngine zipEngine = zipScriptManager.getZipEngine(servletContext);

		String originalTemplateName = templateContext.getTemplate().getName();
		String resolvedTemplateName = templatePaths.get(originalTemplateName);
		hudson.zipscript.template.Template zipTemplate = null;
		if (null == resolvedTemplateName) {
			// get the list of templates we can use
			List<org.apache.struts2.components.template.Template> templates = templateContext.getTemplate()
					.getPossibleTemplates(this);
			for (org.apache.struts2.components.template.Template t : templates) {
				String path = getFinalTemplateName(t);
				try {
					zipTemplate = zipEngine.getTemplate(path);
					templatePaths.put(originalTemplateName, resolvedTemplateName);
				}
				catch (ResourceNotFoundException e) {
					continue;
				}
			}
		}
		else {
			zipTemplate = zipEngine.getTemplate(resolvedTemplateName);
		}

		if (null == zipTemplate) {
			LOG.error("Could not load template " + templateContext.getTemplate());
			return;
		}
		else {
			Object context = zipScriptManager.createContext(actionInvocation, null, req);
			writeOutput(zipTemplate, context, templateContext.getWriter());
		}
	}

	protected String getSuffix() {
		return "zs";
	}

	protected void writeOutput (Template t, Object context, Writer writer) {
		t.merge(context, writer);
	}
}
