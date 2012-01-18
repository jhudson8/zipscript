/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.util;

import java.util.Map;

public class PropertyUtil {

	public static boolean getProperty(String name, boolean defaultValue,
			Map properties) {
		Object obj = properties.get(name);
		if (null == obj)
			return defaultValue;
		else if (obj instanceof Boolean)
			return ((Boolean) obj).booleanValue();
		else if (obj instanceof String) {
			if (obj.toString().equals(Boolean.TRUE.toString()))
				return true;
			else if (obj.toString().equals(Boolean.FALSE.toString()))
				return false;
		}
		return defaultValue;
	}

	public static String getProperty(String name, String defaultValue,
			Map properties) {
		Object obj = properties.get(name);
		if (null == obj)
			return defaultValue;
		else
			return obj.toString();
	}
}
