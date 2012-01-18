/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable.adapter;

import hudson.zipscript.parser.context.Context;

import java.util.Collection;
import java.util.Set;

public class ContextMapAdapter implements HashAdapter {

	public static final ContextMapAdapter INSTANCE = new ContextMapAdapter();

	public boolean appliesTo(Object object) {
		return (object instanceof Context);
	}

	public Object get(Object key, Object map,
			RetrievalContext retrievalContext, String contextHint) {
		return ((Context) map).get(key, retrievalContext, contextHint);
	}

	public Set getKeys(Object map) {
		return ((Context) map).getKeys();
	}

	public Collection getValues(Object map) throws ClassCastException {
		throw new UnsupportedOperationException();
	}

	public void put(Object key, Object value, Object map) {
		((Context) map).put(key, value);
	}

	public Object remove(Object key, Object map) {
		return ((Context) map).remove(key);
	}

	public int getSize(Object map) throws ClassCastException {
		return ((Context) map).getKeys().size();
	}

}
