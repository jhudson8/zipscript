/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element;

import hudson.zipscript.parser.context.ExtendedContext;

import java.io.Writer;

public interface ToStringWithContextElement {

	public String toString(ExtendedContext context);

	public void append(ExtendedContext context, Writer writer);
}
