/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser;

import hudson.zipscript.parser.exception.InitializationException;

import java.util.Map;

public interface Configurable {

	public void configure(Map properties) throws InitializationException;
}
