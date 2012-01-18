/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable.adapter;

import java.util.Iterator;
import java.util.Set;

public class SetAdapter implements SequenceAdapter {

	public static SetAdapter INSTANCE = new SetAdapter();

	public boolean appliesTo(Object object) {
		return (object instanceof Set);
	}

	public int getSize(Object object) {
		return ((Set) object).size();
	}

	public Object getItemAt(int index, Object sequence,
			RetrievalContext retrievalContext, String contextHint) {
		throw new UnsupportedOperationException();
	}

	public Object nextItem(int index, Object lastVal, Object sequence)
			throws ClassCastException {
		if (lastVal == null) {
			Iterator i = ((Set) sequence).iterator();
			Object rtn = i.next();
			return new IteratorSequenceItem(rtn, i);
		} else {
			IteratorSequenceItem isi = (IteratorSequenceItem) lastVal;
			isi.setObject(isi.getIterator().next());
			return isi;
		}
	}

	public boolean contains(Object object, Object sequence) {
		return ((Set) sequence).contains(object);
	}

	public void setItemAt(int index, Object value, Object sequence) {
		throw new UnsupportedOperationException();
	}

	public void addItemAt(int index, Object value, Object sequence) {
		throw new UnsupportedOperationException();
	}

	public boolean hasNext(int index, Object previousItem, Object sequence) {
		return ((Set) sequence).size() > (index + 1);
	}

	public int indexOf(Object object, Object sequence)
			throws ClassCastException {
		throw new UnsupportedOperationException();
	}

	public int lastIndexOf(Object object, Object sequence)
			throws ClassCastException {
		throw new UnsupportedOperationException();
	}
}