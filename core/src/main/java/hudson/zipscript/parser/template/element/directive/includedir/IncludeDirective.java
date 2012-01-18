/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.directive.includedir;

import hudson.zipscript.ResourceContainer;
import hudson.zipscript.parser.Constants;
import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.context.MapContextWrapper;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ElementIndex;
import hudson.zipscript.parser.template.data.ParseParameters;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.directive.AbstractDirective;
import hudson.zipscript.parser.template.element.lang.variable.VariableElement;
import hudson.zipscript.parser.util.ResourceUtil;
import hudson.zipscript.parser.util.TemplateResource;
import hudson.zipscript.resource.Resource;

import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IncludeDirective extends AbstractDirective {

	// dynamic include
	private VariableElement includeElement;
	private Map parsedResources;
	// static include
	private String includePath;
	private TemplateResource includeResource;

	private String contents;
	private int contentStartPosition;
	private ParseParameters parseParameters;

	public IncludeDirective(String contents, ParsingSession session,
			int contentStartPosition) throws ParseException {
		this.contents = contents;
		this.contentStartPosition = contentStartPosition;
	}

	public void validate(ParsingSession session) throws ParseException {
		this.includeElement = new VariableElement(false, true, contents.trim(),
				session.clone(session.getParameters()), contentStartPosition);
		if (this.includeElement.isStatic()) {
			this.includePath = this.includeElement.objectValue(
					new MapContextWrapper(new HashMap())).toString();
			this.includeElement = null;
			this.includeResource = loadTemplateResource(includePath, session
					.getResourceContainer());
		} else {
			this.parsedResources = new HashMap();
		}
		this.parseParameters = session.getParameters();
	}

	public void merge(ExtendedContext context, Writer sw)
			throws ExecutionException {
		if (null != this.includeResource) {
			// statically defined
			if (context.doRefreshTemplates()
					&& includeResource.resource.hasBeenModified()) {
				this.includeResource = loadTemplateResource(this.includePath,
						context.getResourceContainer());
			}
			includeResource.template.merge(context, sw, context.getLocale());
		} else {
			// dynamically defined
			Object includePath = this.includeElement.objectValue(context);
			if (null == includePath)
				throw new ExecutionException("Null resource include '"
						+ includeElement + "'", this);
			String s = includePath.toString();
			TemplateResource tr = (TemplateResource) parsedResources.get(s);
			if (null == tr
					|| (context.doRefreshTemplates() && tr.resource
							.hasBeenModified())) {
				// reload
				tr = loadTemplateResource(s, context.getResourceContainer());
			}
			tr.template.merge(context, sw, context.getLocale());
		}
	}

	private TemplateResource loadTemplateResource(String path,
			ResourceContainer resourceContainer) {
		Resource r = resourceContainer.getIncludeResourceLoader().getResource(
				path,
				resourceContainer.getInitParameters().get(
						Constants.INCLUDE_RESOURCE_LOADER_PARAMETER));
		try {
			return ResourceUtil.loadTemplate(parseParameters,
					resourceContainer, r);
		} catch (ParseException e) {
			throw new ExecutionException(e.getMessage(), this);
		}
	}

	public ElementIndex normalize(int index, List elementList,
			ParsingSession session) throws ParseException {
		return null;
	}

	public List getChildren() {
		return null;
	}

	public String toString() {
		return "[#include " + includePath + "/]";
	}
}