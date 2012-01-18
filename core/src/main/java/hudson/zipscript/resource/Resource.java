/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.resource;

import java.io.InputStream;

public interface Resource {

	public InputStream getInputStream();

	public boolean hasBeenModified();
}
