/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.xml;

import hudson.zipscript.parser.context.AbstractContext;
import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.template.element.lang.variable.adapter.RetrievalContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;

public class DocumentContextWrapper extends AbstractContext {

	private static final DocumentObjectAdapter adapter = DocumentObjectAdapter.INSTANCE;

	private Document document;
	private Map additionaContextValues = new HashMap();

	public DocumentContextWrapper(Document document) {
		this.document = document;
	}

	public Object get(Object key, RetrievalContext retrievalContext,
			String contextHint) {
		Object rtn = null;
		if (key instanceof String)
			rtn = adapter.get((String) key, document, retrievalContext,
					contextHint);
		if (null == rtn)
			rtn = additionaContextValues.get(key);
		return rtn;
	}

	public Object remove(Object key) {
		return additionaContextValues.remove(key);
	}

	public void put(Object key, Object value, boolean travelUp) {
		additionaContextValues.put(key, value);
	}

	public void put(Object key, Object value) {
		additionaContextValues.put(key, value);
	}

	public Set getKeys() {
		return additionaContextValues.keySet();
	}

	public ExtendedContext getTemplateContext() {
		return this;
	}
}