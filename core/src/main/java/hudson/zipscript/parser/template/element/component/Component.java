/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.component;

import hudson.zipscript.parser.template.element.PatternMatcher;

public interface Component {

	public PatternMatcher[] getPatternMatchers();
}
