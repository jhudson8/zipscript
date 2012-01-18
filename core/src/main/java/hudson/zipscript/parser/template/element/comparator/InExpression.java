/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.comparator;

import hudson.zipscript.parser.exception.ExecutionException;

import java.util.Collection;
import java.util.Iterator;

public class InExpression extends AbstractComparatorElement {

	protected boolean compare(Object lhs, Object rhs) {
		if (null == lhs)
			return false;
		else if (rhs instanceof Collection) {
			for (Iterator i = ((Collection) rhs).iterator(); i.hasNext();) {
				if (i.next().equals(lhs))
					return true;
			}
			return false;
		} else if (rhs instanceof Object[]) {
			Object[] arr = (Object[]) rhs;
			for (int i = 0; i < arr.length; i++) {
				if (arr[i].equals(lhs))
					return true;
			}
			return false;
		} else {
			throw new ExecutionException(
					"The right hand side of an in statement must be a list '"
							+ this.toString() + "'", this);
		}
	}

	public int getPriority() {
		return 10;
	}

	public String getComparatorString() {
		return "in";
	}
}
