/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.directive.escape.translate;

import hudson.zipscript.parser.template.element.PatternMatcher;
import hudson.zipscript.parser.template.element.component.Component;

public class TranslateComponent implements Component {

	public PatternMatcher[] getPatternMatchers() {
		return new PatternMatcher[] { new TranslatePatternMatcher(),
				new EndTranslatePatternMatcher() };
	}
}