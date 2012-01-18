/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.resource;

import hudson.zipscript.parser.exception.ExecutionException;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;

public class WebInfResourceLoader extends AbstractResourceLoader {

	private static final String PREFIX = "/WEB-INF/";
	private static final String ALT_PREFIX = "WEB-INF/";

	private ServletContext servletContext;

	public WebInfResourceLoader() {
		super();
	}

	public WebInfResourceLoader(String pathPrefix) {
		super(pathPrefix);
	}

	public WebInfResourceLoader(ServletContext servletContext) {
		super();
		this.servletContext = servletContext;
	}

	public WebInfResourceLoader(String pathPrefix, ServletContext servletContext) {
		super(pathPrefix);
		this.servletContext = servletContext;
	}

	public Resource getResource(String path, Object servletContext) {
		if (null == servletContext)
			servletContext = this.servletContext;
		try {
			URL url = ((ServletContext) servletContext).getResource(PREFIX
					+ getRealPath(path));
			if (null == url) {
				try {
					url = ((ServletContext) servletContext)
							.getResource(ALT_PREFIX + getRealPath(path));
				} catch (MalformedURLException e) {
				}
			}
			if (null == url) {
				throw new ExecutionException("Invalid servlet resource '"
						+ PREFIX + getRealPath(path), null);
			}
			return new URLResource(url);
		} catch (Exception e) {
			throw new ExecutionException("Invalid resource path '" + PREFIX
					+ getRealPath(path) + "'", null, e);
		}
	}
}