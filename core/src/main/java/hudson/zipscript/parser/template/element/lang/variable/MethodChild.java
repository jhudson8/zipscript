/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.lang.variable.adapter.ObjectAdapter;
import hudson.zipscript.parser.template.element.lang.variable.adapter.RetrievalContext;

import java.util.List;

public class MethodChild implements VariableChild {

	private String methodName;
	private List parameters;
	private ObjectAdapter objectAdapter;

	public MethodChild(String name, List parameters) {
		this.methodName = name;
		this.parameters = parameters;
	}

	public Object execute(Object parent, ExtendedContext context)
			throws ExecutionException {
		if (null == parent)
			return null;
		if (null == objectAdapter || !objectAdapter.appliesTo(parent)) {
			objectAdapter = context.getResourceContainer()
					.getVariableAdapterFactory().getObjectAdapter(parent);
		}

		// get the method parameters
		Object[] arr = new Object[parameters.size()];
		for (int i = 0; i < parameters.size(); i++) {
			arr[i] = ((Element) parameters.get(i)).objectValue(context);
		}

		return objectAdapter.call(methodName, arr, parent);

	}

	public String getPropertyName() {
		return null;
	}

	public boolean shouldReturnSomething() {
		return false;
	}

	public void setRetrievalContext(RetrievalContext retrievalContext) {
	}

	public void setContextHint(String contextHint) {
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(methodName);
		sb.append('(');
		for (int i = 0; i < parameters.size(); i++) {
			if (i > 0)
				sb.append(", ");
			sb.append(parameters.get(i));
		}
		sb.append(')');
		return sb.toString();
	}
}