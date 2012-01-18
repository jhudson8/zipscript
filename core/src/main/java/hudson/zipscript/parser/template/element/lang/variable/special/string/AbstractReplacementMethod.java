/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable.special.string;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.template.element.lang.variable.adapter.RetrievalContext;
import hudson.zipscript.parser.template.element.lang.variable.special.SpecialMethod;

public abstract class AbstractReplacementMethod implements SpecialMethod {

	public Object execute(Object source, RetrievalContext retrievalContext,
			String contextHint, ExtendedContext context) throws Exception {
		char[] arr = source.toString().toCharArray();
		char[] charsToReplace = getCharsToReplace();
		String[] replacementStrings = getReplacementStrings();

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arr.length; i++) {
			boolean match = false;
			for (int j = 0; j < charsToReplace.length; j++) {
				if (arr[i] == charsToReplace[j]) {
					// replace
					sb.append(replacementStrings[j]);
					match = true;
					break;
				}
			}
			if (!match)
				sb.append(arr[i]);
		}
		return sb.toString();
	}

	protected abstract char[] getCharsToReplace();

	protected abstract String[] getReplacementStrings();

	public RetrievalContext getExpectedType() {
		return RetrievalContext.TEXT;
	}
}
