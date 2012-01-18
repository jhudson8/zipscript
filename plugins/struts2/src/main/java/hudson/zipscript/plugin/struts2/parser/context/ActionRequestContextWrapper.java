/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.plugin.struts2.parser.context;

import hudson.zipscript.parser.context.AbstractContext;
import hudson.zipscript.parser.exception.UnknownPropertyException;
import hudson.zipscript.parser.template.element.lang.variable.adapter.JavaObjectAdapter;
import hudson.zipscript.parser.template.element.lang.variable.adapter.RetrievalContext;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

public class ActionRequestContextWrapper extends AbstractContext {

	private static final JavaObjectAdapter OBJECT_ADAPTER = new JavaObjectAdapter();

	private Object action;
	private HttpServletRequest request;

	public ActionRequestContextWrapper (
			Object action, HttpServletRequest request) {
		this.action = action;
		this.request = request;
	}

	public void put(Object key, Object value, boolean travelUp) {
		request.setAttribute(key.toString(), value);
	}

	public Object get(Object key, RetrievalContext retrievalContext, String contextHelper) {
		String keyStr = (String) key;
		Object rtn = request.getAttribute(keyStr);
		if (null != rtn) return rtn;
		else {
			// maybe action parameter
			try {
				rtn = OBJECT_ADAPTER.get(keyStr, action, retrievalContext, contextHelper);
			}
			catch (UnknownPropertyException e) {}
			if (null != rtn) {
				request.setAttribute(keyStr, rtn);
			}
			return rtn;
		}
	}

	public Set getKeys() {
		throw new UnsupportedOperationException();
	}

	public void put(Object key, Object value) {
		request.setAttribute((String) key, value);
	}

	public Object remove(Object key) {
		String keyStr = (String) key;
		Object rtn = request.getAttribute(keyStr);
		request.removeAttribute(keyStr);
		return rtn;
	}
}
