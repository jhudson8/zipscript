/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.util;

import hudson.zipscript.parser.Configurable;
import hudson.zipscript.parser.exception.ExecutionException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ClassUtil {

	public static Object loadResource(String prefix, Map properties,
			Class expectedInterface, Class defaultClass, Map types) {
		prefix = prefix + '.';
		Map newProps = new HashMap();
		if (null != properties) {
			for (Iterator i = properties.entrySet().iterator(); i.hasNext();) {
				Map.Entry entry = (Map.Entry) i.next();
				String key = entry.getKey().toString();
				if (key.startsWith(prefix)) {
					String keySub = key.substring(prefix.length());
					newProps.put(keySub, entry.getValue());
				}
			}
		}
		Class clazz = null;
		String type = (String) newProps.get("type");
		if (null != types && null != type) {
			clazz = (Class) types.get(type);
		}
		if (null == clazz) {
			Object className = newProps.get("class");
			if (null != className) {
				if (className instanceof Class) {
					clazz = (Class) className;
				} else {
					try {
						clazz = Thread.currentThread().getContextClassLoader()
								.loadClass(className.toString());
					} catch (Exception e) {
						throw new ExecutionException("The class '"
								+ className.toString()
								+ "' could not be located", null, e);
					}
				}
			}
		}
		if (null == clazz) {
			if (null == defaultClass)
				return null;
			else
				clazz = defaultClass;
		}
		try {
			Object obj = clazz.newInstance();
			if (isInstanceOf(clazz, expectedInterface)) {
				if (obj instanceof Configurable) {
					((Configurable) obj).configure(newProps);
				}
				return obj;
			} else {
				throw new ExecutionException("The class '" + clazz
						+ "' must implement '" + expectedInterface + "'", null);
			}
		} catch (Exception e) {
			throw new ExecutionException("An error occured while loading '"
					+ clazz.getName() + "'", null, e);
		}
	}

	private static boolean isInstanceOf(Class checkClass, Class interfaceClass) {
		if (checkClass.equals(interfaceClass))
			return true;
		for (int i = 0; i < checkClass.getInterfaces().length; i++) {
			if (isInstanceOf(checkClass.getInterfaces()[i], interfaceClass))
				return true;
		}
		Class parentClass = checkClass.getSuperclass();
		if (parentClass.equals(Object.class))
			return false;
		else
			return isInstanceOf(parentClass, interfaceClass);
	}
}