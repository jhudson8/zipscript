/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.util.i18n;

import java.util.List;

public class I18NResourceImpl implements I18NResource {

	public String get(String key, List parameters) {
		return key;
	}

	public String get(String key, Object parameter) {
		return key;
	}

	public String get(String key, Object[] parameters) {
		return key;
	}

	public String get(String key) {
		return key;
	}
}
