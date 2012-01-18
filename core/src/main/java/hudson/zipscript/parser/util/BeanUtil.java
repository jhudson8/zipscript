/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.util;

import java.lang.reflect.Method;

public class BeanUtil {

	public static Method getPropertyMethod(Object bean, String name,
			Object[] parameters) {
		if (bean == null) {
			return null;
		}
		if (name == null) {
			throw new IllegalArgumentException(
					"No name specified for bean class '" + bean.getClass()
							+ "'");
		}
		Class clazz = bean.getClass();
		if (clazz.getName().indexOf("EnhancerByCGLIB") > 0) {
			try {
				clazz = Thread.currentThread().getContextClassLoader().loadClass(clazz.getName().substring(0, clazz.getName().indexOf('$')));
			}
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		if (null == parameters || parameters.length == 0) {
			// simple getter
			Method m = null;
			try {
				m = clazz.getMethod("get"
						+ StringUtil.firstLetterUpperCase(name), new Class[0]);
			} catch (NoSuchMethodException e1) {
				try {
					m = clazz.getMethod("get" + name, new Class[0]);
				} catch (NoSuchMethodException e2) {
					try {
						m = clazz.getMethod(StringUtil
								.firstLetterLowerCase(name), new Class[0]);
					} catch (NoSuchMethodException e3) {
						return null;
					}
				}
			}
			return m;
		} else {
			for (int i = 0; i < clazz.getMethods().length; i++) {
				Method m = clazz.getMethods()[i];
				if (m.getName().equals(name)
						&& m.getParameterTypes().length == parameters.length) {
					// probably a good fit
					boolean isOk = true;
					for (int j = 0; j < parameters.length; j++) {
						if (null != parameters[j])
							isOk = isOk
									&& doesParameterFit(parameters[j]
											.getClass(),
											m.getParameterTypes()[j]);
						if (!isOk)
							break;
					}
					if (isOk)
						return m;
				}
			}
			return null;
		}
	}

	private static boolean doesParameterFit(Class obj, Class type) {
		if (null == obj)
			return true;
		if (type.isPrimitive()) {
			if (type.toString().equals("short"))
				type = Short.class;
			else if (type.toString().equals("int"))
				type = Integer.class;
			else if (type.toString().equals("long"))
				type = Long.class;
			else if (type.toString().equals("float"))
				type = Float.class;
			else if (type.toString().equals("double"))
				type = Double.class;
			else if (type.toString().equals("char"))
				type = Character.class;
			else if (type.toString().equals("boolean"))
				type = Boolean.class;
		}
		if (obj.equals(type))
			return true;
		else {
			// check super classes
			Class parent = obj.getSuperclass();
			if (null != parent) {
				if (doesParameterFit(parent, type))
					return true;
			}
			// check interfaces
			for (int i = 0; i < obj.getInterfaces().length; i++) {
				if (doesParameterFit(obj.getInterfaces()[i], type))
					return true;
			}
			return false;
		}
	}
}