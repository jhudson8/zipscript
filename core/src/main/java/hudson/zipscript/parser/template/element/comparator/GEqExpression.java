/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.comparator;

public class GEqExpression extends AbstractComparatorElement {

	protected boolean compare(Object lhs, Object rhs) {
		if (null == lhs || null == rhs)
			return false;
		if (lhs.equals(rhs))
			return true;
		if (!(lhs instanceof Number) || !(rhs instanceof Number))
			return false;
		return ((Number) lhs).doubleValue() > ((Number) rhs).doubleValue();
	}

	public int getPriority() {
		return 10;
	}

	public String getComparatorString() {
		return ">=";
	}
}
