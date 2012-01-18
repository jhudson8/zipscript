/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package test.hudson.zipscript.model;

import java.util.ArrayList;
import java.util.List;

public class Obj2 {
	static List list1 = new ArrayList();
	static List list2 = new ArrayList();
	static {
		list1.add(new Obj3("foo"));
		list1.add(new Obj3("bar"));
		list1.add(new Obj3("baz"));
		list2.add("foo");
		list2.add("bar");
		list2.add("baz");
	}

	public List getList1() {
		return list1;
	}

	public List getList2() {
		return list2;
	}

	public String toString() {
		return getClass().getName();
	}
}
