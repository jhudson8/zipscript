/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.util.uniqueid;

import hudson.zipscript.parser.Configurable;
import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.template.element.ToStringWithContextElement;

public interface UniqueIdGenerator extends ToStringWithContextElement,
		Configurable {

	public String toString(ExtendedContext context);
}
