/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.ext.data;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

public class RequestParameterMap implements Map {

	HttpServletRequest request;

	public RequestParameterMap(HttpServletRequest request) {
		this.request = request;
	}

	public String get(String key) {
		String s = request.getParameter(key);
		if (null == s || s.length() == 0) return null;
		else return s;
	}

	public String[] values(String key) {
		return request.getParameterValues(key);
	}

	public void clear() {
		throw new UnsupportedOperationException();
	}

	public boolean containsKey(Object key) {
		return request.getParameterMap().containsKey(key);
	}

	public boolean containsValue(Object value) {
		return request.getParameterMap().containsValue(value);
	}

	public Set entrySet() {
		return request.getParameterMap().entrySet();
	}

	public Object get(Object key) {
		if (null == key)
			return null;
		else {
			String s = request.getParameter(key.toString());
			if (null == s || s.length() == 0) return null;
			else return s;
		}
	}

	public boolean isEmpty() {
		return request.getParameterMap().isEmpty();
	}

	public Set keySet() {
		return request.getParameterMap().keySet();
	}

	public Object put(Object key, Object value) {
		throw new UnsupportedOperationException();
	}

	public void putAll(Map t) {
		throw new UnsupportedOperationException();
	}

	public Object remove(Object key) {
		throw new UnsupportedOperationException();
	}

	public int size() {
		return request.getParameterMap().size();
	}

	public Collection values() {
		return request.getParameterMap().values();
	}
}
