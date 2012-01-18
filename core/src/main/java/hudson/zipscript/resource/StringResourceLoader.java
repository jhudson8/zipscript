/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.resource;

import java.io.ByteArrayInputStream;

public class StringResourceLoader extends AbstractResourceLoader {

	public static StringResourceLoader INSTANCE = new StringResourceLoader();

	public Resource getResource(String path, Object parameter) {
		return new StreamOnlyResource(new ByteArrayInputStream(path.getBytes()));
	}
}