/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable.special.string;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.lang.variable.adapter.RetrievalContext;
import hudson.zipscript.parser.template.element.lang.variable.special.SpecialMethod;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class SplitSpecialMethod implements SpecialMethod {

	private Element splitToken;

	public SplitSpecialMethod(Element[] params) {
		this.splitToken = params[0];
	}

	public Object execute(Object source, RetrievalContext retrievalContext,
			String contextHint, ExtendedContext context) throws Exception {
		String s = source.toString();
		String split = splitToken.objectValue(context).toString();

		StringTokenizer st = new StringTokenizer(s, split);
		java.util.List l = new ArrayList();
		while (st.hasMoreElements())
			l.add(st.nextElement());
		return l.toArray(new String[l.size()]);
	}

	public RetrievalContext getExpectedType() {
		return RetrievalContext.TEXT;
	}
}
