/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable;

import hudson.zipscript.parser.context.Context;
import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.context.ZSContextRequiredGetter;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.template.element.lang.variable.adapter.HashAdapter;
import hudson.zipscript.parser.template.element.lang.variable.adapter.ObjectAdapter;
import hudson.zipscript.parser.template.element.lang.variable.adapter.RetrievalContext;

public class PropertyChild implements VariableChild {

	private String name;
	private RetrievalContext retrievalContext;
	private String contextHint;

	private HashAdapter mapAdapter;
	private ObjectAdapter objectAdapter;

	private short type = Short.MIN_VALUE;
	private static short TYPE_CONTEXT = 1;
	private static short TYPE_CONTEXT_REQUIRED_GETTER = 2;
	private static short TYPE_MAP = 3;
	private static short TYPE_OBJECT = 4;

	private boolean doTypeChecking = false;

	public PropertyChild(String name) {
		this.name = name;
	}

	public Object execute(Object parent, ExtendedContext context)
			throws ExecutionException {
		if (null == parent)
			return null;
		if (doTypeChecking || type == Short.MIN_VALUE) {
			type = Short.MIN_VALUE;
			if (parent instanceof ZSContextRequiredGetter)
				type = TYPE_CONTEXT_REQUIRED_GETTER;
			else if (parent instanceof Context)
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
						objectAdapter = context.getResourceContainer()
								.getVariableAdapterFactory().getObjectAdapter(
										parent);
						type = TYPE_OBJECT;
					}
				}
			}
		}

		if (doTypeChecking) {
			// dont' worry about ClassCast because we just detected type
			if (type == TYPE_CONTEXT) {
				return ((Context) parent).get(name, retrievalContext,
						contextHint);
			} else if (type == TYPE_CONTEXT_REQUIRED_GETTER) {
				return ((ZSContextRequiredGetter) parent).get(name,
						retrievalContext, contextHint, context);
			} else if (type == TYPE_MAP) {
				return mapAdapter.get(name, parent, retrievalContext,
						contextHint);
			} else
				return objectAdapter.get(name, parent, retrievalContext,
						contextHint);
		} else {
			try {
				if (type == TYPE_CONTEXT) {
					return ((Context) parent).get(name, retrievalContext,
							contextHint);
				} else if (type == TYPE_CONTEXT_REQUIRED_GETTER) {
					return ((ZSContextRequiredGetter) parent).get(name,
							retrievalContext, contextHint, context);
				} else if (type == TYPE_MAP) {
					return mapAdapter.get(name, parent, retrievalContext,
							contextHint);
				} else
					return objectAdapter.get(name, parent, retrievalContext,
							contextHint);
			} catch (ClassCastException e) {
				this.doTypeChecking = true;
				return this.execute(parent, context);
			}
		}
	}

	public String getPropertyName() {
		return name;
	}

	public boolean shouldReturnSomething() {
		return true;
	}

	public String toString() {
		return name;
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