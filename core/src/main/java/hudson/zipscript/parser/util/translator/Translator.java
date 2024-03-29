/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.util.translator;

import hudson.zipscript.parser.Configurable;

import java.util.List;
import java.util.Locale;

public interface Translator extends Configurable {

	public List translate(List elementsOrText, Locale to) throws Exception;

	public String getBaseLocaleKey();
}
