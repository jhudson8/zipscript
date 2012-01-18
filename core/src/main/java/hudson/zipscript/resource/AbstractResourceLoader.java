/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.resource;

import hudson.zipscript.parser.exception.InitializationException;

import java.util.Map;

public abstract class AbstractResourceLoader implements ResourceLoader {

	private String pathPrefix;

	public AbstractResourceLoader() {
	}

	public AbstractResourceLoader(String pathPrefix) {
		this.pathPrefix = pathPrefix;
	}

	public void configure(Map properties) throws InitializationException {
		pathPrefix = (String) properties.get("pathPrefix");
	}

	protected String getRealPath(String path) {
		if (null == pathPrefix)
			return path;
		else
			return pathPrefix + path;
	}
}
