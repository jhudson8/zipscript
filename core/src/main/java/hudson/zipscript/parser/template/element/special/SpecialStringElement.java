/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.special;

import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.group.MapElement;

public interface SpecialStringElement extends Element {

	public String getTokenValue();

	public MapElement getLastMapElement();

	public String getPropertyName();
}
