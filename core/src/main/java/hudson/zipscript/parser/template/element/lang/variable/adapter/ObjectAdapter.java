/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable.adapter;

import java.util.Iterator;

public interface ObjectAdapter {

	public boolean appliesTo(Object object);

	public Object get(String key, Object object, RetrievalContext context,
			String contextHint) throws ClassCastException;

	public void set(String key, Object value, Object object)
			throws ClassCastException;

	public Iterator getProperties(Object object) throws ClassCastException;

	public Object call(String key, Object[] parameters, Object object)
			throws ClassCastException;
}
