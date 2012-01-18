/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.lang.variable.adapter.RetrievalContext;
import hudson.zipscript.parser.template.element.lang.variable.adapter.VariableAdapterFactory;
import hudson.zipscript.parser.util.BeanUtil;

import java.lang.reflect.Method;
import java.util.List;

public class AssumedGetRoot implements VariableChild {

	private short TYPE_STANDARD = 1;
	private short TYPE_RESERVED = 2;

	private Element variableElement;
	private String name;
	private List parameters;
	private Method accessorMethod;
	private short type = TYPE_STANDARD;
	private VariableAdapterFactory variableAdapterFactory;
	private RetrievalContext retrievalContext;
	private String contextHint;

	private boolean doTypeChecking = false;

	public AssumedGetRoot(String name, List parameters,
			Element variableElement,
			VariableAdapterFactory variableAdapterFactory) {
		this.name = name;
		this.parameters = parameters;
		this.variableElement = variableElement;
		this.variableAdapterFactory = variableAdapterFactory;
		for (int i = 0; i < variableAdapterFactory
				.getReservedContextAttributes().length; i++) {
			if (name.equals(variableAdapterFactory
					.getReservedContextAttributes()[i])) {
				type = TYPE_RESERVED;
			}
		}
	}

	public Object execute(Object parent, ExtendedContext context)
			throws ExecutionException {
		Object source = null;
		if (type == TYPE_STANDARD)
			source = context.get(name, retrievalContext, contextHint);
		else
			source = context.getRootContext().get(name, retrievalContext,
					contextHint);
		if (null == source)
			return null;

		// get the method parameters
		Object[] arr = new Object[parameters.size()];
		for (int i = 0; i < parameters.size(); i++) {
			arr[i] = ((Element) parameters.get(i)).objectValue(context);
		}

		if (null == accessorMethod || doTypeChecking) {
			String methodName = variableAdapterFactory
					.getDefaultGetterMethod(source);

			// initialize
			accessorMethod = BeanUtil
					.getPropertyMethod(source, methodName, arr);
			if (null == accessorMethod) {
				throw new ExecutionException("Unknown method '" + methodName
						+ "' on " + variableElement.toString(), null);
			}
		}

		try {
			return accessorMethod.invoke(source, arr);
		} catch (ClassCastException e) {
			this.doTypeChecking = true;
			return execute(parent, context);
		} catch (IllegalArgumentException e) {
			this.doTypeChecking = true;
			return execute(parent, context);
		} catch (Exception e) {
			throw new ExecutionException(e.getMessage(), variableElement, e);
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
		sb.append(name);
		sb.append('(');
		for (int i = 0; i < parameters.size(); i++) {
			if (i > 0)
				sb.append(", ");
			sb.append(parameters.get(i));
		}
		sb.append(')');
		return sb.toString();
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