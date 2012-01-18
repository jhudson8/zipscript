/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.resource;

import java.io.InputStream;

public class StreamOnlyResource extends AbstractResource {

	private InputStream is;

	public StreamOnlyResource(InputStream is) {
		this.is = is;
	}

	public InputStream getInputStream() {
		return is;
	}

	public boolean hasBeenModified() {
		return false;
	}
}
