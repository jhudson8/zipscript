/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.ext.data;

import hudson.zipscript.parser.util.StringUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ResultData implements Map {

	private static final String LAYOUT = "layout";
	private static final String PAGE = "page";

	private String template;
	private String page;
	private String layout;
	private Map parameters;

	public ResultData(String templateLocation) {
		int index = templateLocation.indexOf(']');
		if (index >= 0 && templateLocation.charAt(0) == '[') {
			this.template = templateLocation.substring(index + 1).trim();
			loadParams(templateLocation.substring(1, index));
		} else {
			this.template = templateLocation;
		}
	}

	private void loadParams(String params) {
		this.parameters = StringUtil.getProperties(params);
		if (null != this.parameters) {
			this.layout = (String) this.parameters.get(LAYOUT);
			this.page = (String) this.parameters.get(PAGE);
		}
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getLayout() {
		return layout;
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}

	public String getParameter(String key) {
		return (String) get(key);
	}

	public void clear() {
		if (null != parameters)
			parameters.clear();
	}

	public boolean containsKey(Object key) {
		if (null == parameters)
			return false;
		else
			return parameters.containsKey(key);
	}

	public boolean containsValue(Object value) {
		if (null == parameters)
			return false;
		else
			return parameters.containsValue(value);
	}

	public Set entrySet() {
		if (null == parameters)
			return Collections.EMPTY_SET;
		else
			return parameters.entrySet();
	}

	public Object get(Object key) {
		if (null == parameters)
			return null;
		else
			return parameters.get(key);
	}

	public boolean isEmpty() {
		if (null == parameters)
			return true;
		else
			return parameters.isEmpty();
	}

	public Set keySet() {
		if (null == parameters)
			return Collections.EMPTY_SET;
		else
			return parameters.keySet();
	}

	public Object put(Object arg0, Object arg1) {
		if (null == parameters)
			parameters = new HashMap();
		return parameters.put(arg0, arg1);
	}

	public void putAll(Map arg0) {
		if (null == parameters)
			parameters = new HashMap();
		parameters.putAll(arg0);
	}

	public Object remove(Object key) {
		if (null == parameters)
			return null;
		else
			return parameters.remove(key);
	}

	public int size() {
		if (null == parameters)
			return 0;
		else
			return parameters.size();
	}

	public Collection values() {
		if (null == parameters)
			return null;
		else
			return Collections.EMPTY_LIST;
	}
}