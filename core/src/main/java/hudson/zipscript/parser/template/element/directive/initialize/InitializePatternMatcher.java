/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.directive.initialize;

import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.directive.AbstractDirectivePatternMatcher;

public class InitializePatternMatcher extends AbstractDirectivePatternMatcher {

	protected Element createElement(char[] startToken, String s,
			int contentIndex, ParsingSession parsingSession)
			throws ParseException {
		return new InitializeDirective();
	}

	protected String getDirectiveName() {
		return "initialize";
	}

	protected boolean allowEmpty() {
		return true;
	}

	protected boolean onlyAllowEmpty() {
		return true;
	}
}