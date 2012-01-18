/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable.adapter;

import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.exception.UnknownPropertyException;
import hudson.zipscript.parser.util.BeanUtil;
import hudson.zipscript.parser.util.StringUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JavaObjectAdapter implements ObjectAdapter {

	private static final Object[] NO_PARAMS = new Object[0];
	private Map getMethods = new HashMap();

	public boolean appliesTo(Object object) {
		return true;
	}

	public Object call(String key, Object[] parameters, Object object) {
		Method m = BeanUtil.getPropertyMethod(object, key, parameters);
		if (null != m) {
			try {
				return m.invoke(object, parameters);
			} catch (Exception e) {
				throw new ExecutionException(e.getMessage(), null, e);
			}
		} else {
			throw new UnknownPropertyException(key, object);
		}
	}

	public Object get(String key, Object object, RetrievalContext context,
			String contextHint) {
		Object obj = getMethods.get(key);
		if (null == getMethods.get(key)) {
			Method method = BeanUtil.getPropertyMethod(object, key, NO_PARAMS);
			if (null != method) {
				getMethods.put(key, method);
				obj = method;
			} else {
				UnknownPropertyException e = new UnknownPropertyException(key,
						object);
				getMethods.put(key, e);
				obj = e;
			}
		}
		if (obj instanceof UnknownPropertyException) {
			throw (UnknownPropertyException) obj;
		} else {
			try {
				return ((Method) obj).invoke(object, NO_PARAMS);
			}
			catch (IllegalArgumentException e) {
				throw new ClassCastException(e.getMessage());
			} catch (Exception e) {
				throw new ExecutionException(e.getMessage(), null, e);
			}
		}
	}

	public Iterator getProperties(Object object) {
		return null;
	}

	public void set(String key, Object value, Object object) {
		Object[] params = new Object[] { value };
		Method m = BeanUtil.getPropertyMethod(object, "set"
				+ StringUtil.firstLetterUpperCase(key), params);
		if (null != m) {
			try {
				m.invoke(object, params);
			} catch (Exception e) {
				throw new ExecutionException(e.getMessage(), null, e);
			}
		}
	}
}