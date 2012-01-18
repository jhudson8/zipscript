/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable.adapter;

import java.util.Iterator;

public class IteratorSequenceItem implements SequenceItem {

	private Object object;
	private Iterator iterator;

	public IteratorSequenceItem(Object object, Iterator iterator) {
		this.object = object;
		this.iterator = iterator;
	}

	public Object getObject() {
		return object;
	}

	public Iterator getIterator() {
		return iterator;
	}

	public void setIterator(Iterator iterator) {
		this.iterator = iterator;
	}

	public void setObject(Object object) {
		this.object = object;
	}
}