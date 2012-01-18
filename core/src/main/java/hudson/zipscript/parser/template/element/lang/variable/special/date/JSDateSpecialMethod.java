/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable.special.date;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.template.element.lang.variable.adapter.RetrievalContext;
import hudson.zipscript.parser.template.element.lang.variable.special.SpecialMethod;

import java.text.SimpleDateFormat;
import java.util.Date;

public class JSDateSpecialMethod implements SpecialMethod {

	public static final JSDateSpecialMethod INSTANCE = new JSDateSpecialMethod();
	SimpleDateFormat sdf = new SimpleDateFormat("'new Date('yyyy, MM, dd')'");

	public Object execute(Object source, RetrievalContext retrievalContext,
			String contextHint, ExtendedContext context) throws Exception {
		return sdf.format((Date) source);
	}

	public RetrievalContext getExpectedType() {
		return RetrievalContext.TEXT;
	}
}