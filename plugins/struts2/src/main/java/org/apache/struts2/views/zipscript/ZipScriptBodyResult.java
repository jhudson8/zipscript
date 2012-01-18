/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package org.apache.struts2.views.zipscript;

import hudson.zipscript.ZipEngine;
import hudson.zipscript.ext.data.ResultData;
import hudson.zipscript.parser.context.Context;
import hudson.zipscript.template.Template;

import java.io.Writer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.ValueStack;


public class ZipScriptBodyResult extends ZipScriptResult {
	private static final long serialVersionUID = -3624317866978957286L;

	public static final String LAYOUT_PATH_PREFIX = "layouts/";
	public static final String PAGE_PATH = LAYOUT_PATH_PREFIX + "page.zs";

	@Override
	protected void writeOutput(Context context, ValueStack stack,
			ZipEngine zipEngine, ActionInvocation invocation, ResultData resultData,
			ServletContext servletContext, HttpServletRequest request,
			HttpServletResponse response, Writer writer) throws Exception {

		// get the body content
		Template body = getBodyTemplate(
				stack, zipEngine, invocation, resultData, servletContext);
		context = body.initialize(context);

		// get the page content
		Template page = getPageTemplate(
				stack, zipEngine, invocation, resultData, servletContext);
		context.put(getLayoutPlaceholderToken(), body);
		page.merge(context, writer, request.getLocale());
	}

	protected Template getPageTemplate(ValueStack stack, ZipEngine zipEngine,
			ActionInvocation invocation, ResultData resultData, ServletContext servletContext)
			throws Exception {
		if (null != resultData.getPage())
			return zipEngine.getTemplate(LAYOUT_PATH_PREFIX + resultData.getPage(), servletContext);
		else
			return zipEngine.getTemplate(getPagePath(), servletContext);
	}

	protected Template getBodyTemplate(ValueStack stack, ZipEngine zipEngine,
			ActionInvocation invocation, ResultData resultData, ServletContext servletContext)
			throws Exception {
		return super.getPageTemplate(
				stack, zipEngine, invocation, resultData.getTemplate(), servletContext);
	}

	private static final String LAYOUT_PLACEHOLDER = "layout_placeholder";
	protected String getLayoutPlaceholderToken () {
		return LAYOUT_PLACEHOLDER;
	}

	protected String getPagePath () {
		return PAGE_PATH;
	}
}