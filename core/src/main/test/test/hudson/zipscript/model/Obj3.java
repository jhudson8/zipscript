/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package test.hudson.zipscript.model;

public class Obj3 {
	public String text;

	public Obj3(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public String toString() {
		return getClass().getName();
	}

	public int getVal () {
		return 2;
	}
}
