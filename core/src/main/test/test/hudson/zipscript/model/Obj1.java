/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package test.hudson.zipscript.model;

import java.util.HashMap;
import java.util.Map;

public class Obj1 {
	static Map testMap = new HashMap();
	static {
		testMap.put("anotherObj", new Obj2());
	}

	public Map getTestMap() {
		return testMap;
	}

	public Integer getTestInteger() {
		return new Integer(1);
	}

	public Long getTestLong() {
		return new Long(1);
	}

	public Float getTestFloat() {
		return new Float(1);
	}

	public Double getTestDouble() {
		return new Double(1);
	}

	public String toString() {
		return getClass().getName();
	}
}
