/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.resource;

import java.io.File;

public class FileResourceLoader extends AbstractResourceLoader {

	public FileResourceLoader() {
		super();
	}

	public FileResourceLoader(String pathPrefix) {
		super(pathPrefix);
	}

	public Resource getResource(String path, Object parameter) {
		return new FileResource(new File(getRealPath(path)));
	}
}