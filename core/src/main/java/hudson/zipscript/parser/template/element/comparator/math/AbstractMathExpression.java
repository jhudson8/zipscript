/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.comparator.math;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.template.element.comparator.AbstractComparatorElement;

import java.math.BigDecimal;

public abstract class AbstractMathExpression extends AbstractComparatorElement {

	protected boolean compare(Object lhs, Object rhs) {
		return false;
	}

	public Object objectValue(ExtendedContext context)
			throws ExecutionException {
		if (null == getLeftHandSide() || null == getRightHandSide())
			throw new ExecutionException("Invalid expression", this);
		Object lhs = getLeftHandSide().objectValue(context);
		Object rhs = getRightHandSide().objectValue(context);
		if (null == lhs)
			return rhs;
		if (null == rhs)
			return lhs;
		if (!(lhs instanceof Number) || !(rhs instanceof Number)) {
			throw new ExecutionException("Invalid math operator value", this);
		}
		return performOperation((Number) lhs, (Number) rhs);
	}

	protected abstract Object performOperation(Number lhs, Number rhs);

	public int getPriority() {
		return 2;
	}

	protected Class getCommonDenominatorClass(Number lhs, Number rhs) {
		if (lhs instanceof Double || rhs instanceof Double)
			return Double.class;
		else if (lhs instanceof Float || rhs instanceof Float)
			return Float.class;
		else if (lhs instanceof BigDecimal || rhs instanceof BigDecimal)
			return BigDecimal.class;
		else if (lhs instanceof Long || rhs instanceof Long)
			return Long.class;
		else if (lhs instanceof Short || rhs instanceof Short)
			return Short.class;
		else
			return Integer.class;
	}
}
