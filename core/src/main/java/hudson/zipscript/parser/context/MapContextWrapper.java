/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.context;

import hudson.zipscript.parser.template.element.lang.variable.adapter.RetrievalContext;

import java.util.Map;
import java.util.Set;

public class MapContextWrapper extends AbstractContext {

	private Map map;

	public MapContextWrapper(Map map) {
		this.map = map;
	}

	public Object get(Object key, RetrievalContext retrievalContext,
			String contextHint) {
		return map.get(key);
	}

	public Object remove(Object key) {
		return map.remove(key);
	}

	public void put(Object key, Object value, boolean travelUp) {
		map.put(key, value);
	}

	public void put(Object key, Object value) {
		this.put(key, value, false);
	}

	public Set getKeys() {
		return map.keySet();
	}

	public ExtendedContext getTemplateContext() {
		return this;
	}
}