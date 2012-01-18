/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.resource;

import hudson.zipscript.parser.Configurable;

public interface ResourceLoader extends Configurable {

	public Resource getResource(String path, Object parameter);
}
