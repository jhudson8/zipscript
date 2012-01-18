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

public class ZipScriptLayoutResult extends ZipScriptBodyResult {
	private static final long serialVersionUID = -3624317866978957286L;

	private static final String LAYOUT_PATH = LAYOUT_PATH_PREFIX + "layout.zs";

	@Override
	protected void writeOutput(Context context, ValueStack stack,
			ZipEngine zipEngine, ActionInvocation invocation, ResultData resultData,
			ServletContext servletContext, HttpServletRequest request,
			HttpServletResponse response, Writer writer) throws Exception {

		// get the body content
		Template body = getBodyTemplate(
				stack, zipEngine, invocation, resultData, servletContext);
		context = body.initialize(context);
		context.put(getScreenPlaceholderToken(), body);

		// get the layout content
		Template layout = getLayoutTemplate(
				stack, zipEngine, invocation, resultData, servletContext);
		layout.initialize(context);
		context.put(getLayoutPlaceholderToken(), layout);

		// get the page content
		Template page = getPageTemplate(
				stack, zipEngine, invocation, resultData, servletContext);
		page.merge(context, writer, request.getLocale());
	}

	protected Template getLayoutTemplate(ValueStack stack, ZipEngine zipEngine,
			ActionInvocation invocation, ResultData resultData, ServletContext servletContext)
			throws Exception {
		if (null != resultData.getLayout())
			return zipEngine.getTemplate(LAYOUT_PATH_PREFIX + resultData.getLayout(), servletContext);
		else
			return zipEngine.getTemplate(getLayoutPath(), servletContext);
	}

	private static final String SCREEN_PLACEHOLDER = "screen_placeholder";
	protected String getScreenPlaceholderToken () {
		return SCREEN_PLACEHOLDER;
	}

	protected String getLayoutPath () {
		return LAYOUT_PATH;
	}
}