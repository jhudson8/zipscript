/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable.special.string;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.lang.variable.adapter.RetrievalContext;
import hudson.zipscript.parser.template.element.lang.variable.special.SpecialMethod;

import java.net.URLEncoder;

public class URLSpecialMethod implements SpecialMethod {

	private String encoding;

	public URLSpecialMethod(ParsingSession parsingSession) {
		this.encoding = "UTF-8";
	}

	public Object execute(Object source, RetrievalContext retrievalContext,
			String contextHint, ExtendedContext context) throws Exception {
		return URLEncoder.encode((String) source, encoding);
	}

	public RetrievalContext getExpectedType() {
		return RetrievalContext.TEXT;
	}
}