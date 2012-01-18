/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.resource;

import hudson.zipscript.parser.exception.ExecutionException;

import java.io.InputStream;

public class ClasspathResourceLoader extends AbstractResourceLoader {

	public ClasspathResourceLoader() {
		super();
	}

	public ClasspathResourceLoader(String pathPrefix) {
		super(pathPrefix);
	}

	public Resource getResource(String path, Object parameter) {
		InputStream is = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(getRealPath(path));
		if (null == is) {
			is = ClassLoader.getSystemResourceAsStream(getRealPath(path));
		}
		if (null == is) {
			throw new ExecutionException("Invalid classpath resource '"
					+ getRealPath(path), null);
		}
		return new StreamOnlyResource(is);
	}
}