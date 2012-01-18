/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable;

import hudson.zipscript.parser.context.Context;
import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.group.MapElement;
import hudson.zipscript.parser.template.element.lang.variable.adapter.HashAdapter;
import hudson.zipscript.parser.template.element.lang.variable.adapter.ObjectAdapter;
import hudson.zipscript.parser.template.element.lang.variable.adapter.RetrievalContext;
import hudson.zipscript.parser.template.element.lang.variable.adapter.SequenceAdapter;

public class MapChild implements VariableChild {

	private static final int TYPE_CONTEXT = 1;
	private static final int TYPE_SEQUENCE = 2;
	private static short TYPE_MAP = 3;
	private static short TYPE_OBJECT = 4;

	private short type = Short.MIN_VALUE;
	private boolean doTypeChecking = false;

	private HashAdapter mapAdapter;
	private SequenceAdapter sequenceAdapter;
	private ObjectAdapter objectAdapter;

	private MapElement mapElement;
	private Element keyElement;
	private RetrievalContext retrievalContext;
	private String contextHint;

	public MapChild(MapElement mapElement, Element keyElement) {
		this.mapElement = mapElement;
		this.keyElement = keyElement;
	}

	public Object execute(Object parent, ExtendedContext context)
			throws ExecutionException {
		if (null == parent)
			return null;
		if (doTypeChecking || type == Short.MIN_VALUE) {
			type = Short.MIN_VALUE;
			if (parent instanceof Context)
				type = TYPE_CONTEXT;
			if (type == Short.MIN_VALUE) {
				if (null != mapAdapter && mapAdapter.appliesTo(parent)) {
					type = TYPE_MAP;
				}
				if (type == Short.MIN_VALUE) {
					mapAdapter = context.getResourceContainer()
							.getVariableAdapterFactory().getHashAdapter(parent);
					if (null != mapAdapter) {
						type = TYPE_MAP;
					} else {
						if (null != sequenceAdapter
								&& sequenceAdapter.appliesTo(parent)) {
							type = TYPE_SEQUENCE;
						}
						if (type == Short.MIN_VALUE) {
							sequenceAdapter = context.getResourceContainer()
									.getVariableAdapterFactory()
									.getSequenceAdapter(parent);
							if (null != sequenceAdapter) {
								type = TYPE_SEQUENCE;
							} else {
								objectAdapter = context.getResourceContainer()
										.getVariableAdapterFactory()
										.getObjectAdapter(parent);
								type = TYPE_OBJECT;
							}
						}
					}
				}
			}
		}

		if (doTypeChecking) {
			return getValue(parent, context);
		} else {
			try {
				return getValue(parent, context);
			} catch (ClassCastException e) {
				this.doTypeChecking = true;
				return execute(parent, context);
			}
		}
	}

	private Object getValue(Object parent, ExtendedContext context) {
		Object key = keyElement.objectValue(context);
		if (null == key)
			throw new ExecutionException(
					"Map or sequence key evaluated to null", null);
		if (type == TYPE_CONTEXT) {
			return context.get(key, retrievalContext, contextHint);
		} else if (type == TYPE_SEQUENCE) {
			if (!(key instanceof Number))
				throw new ExecutionException("Invalid type key", keyElement);
			return sequenceAdapter.getItemAt(((Number) key).intValue(), parent,
					retrievalContext, contextHint);
		} else if (type == TYPE_MAP) {
			return mapAdapter.get(key, parent, retrievalContext, contextHint);
		} else {
			return objectAdapter.get(key.toString(), parent, retrievalContext,
					contextHint);
		}
	}

	public String getPropertyName() {
		return null;
	}

	public boolean shouldReturnSomething() {
		return true;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append('[');
		sb.append(keyElement.toString());
		sb.append(']');
		return sb.toString();
	}

	public MapElement getMapElement () {
		return mapElement;
	}
	
	public Element getKeyElement() {
		return keyElement;
	}

	public void setKeyElement(Element keyElement) {
		this.keyElement = keyElement;
	}

	public RetrievalContext getRetrievalContext() {
		return retrievalContext;
	}

	public void setRetrievalContext(RetrievalContext retrievalContext) {
		this.retrievalContext = retrievalContext;
	}

	public String getContextHint() {
		return contextHint;
	}

	public void setContextHint(String contextHint) {
		this.contextHint = contextHint;
	}
}