/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable.adapter;

import java.util.Collection;
import java.util.Iterator;

public class CollectionAdapter implements SequenceAdapter {

	public static CollectionAdapter INSTANCE = new CollectionAdapter();

	public boolean appliesTo(Object object) {
		return (object instanceof Collection);
	}

	public int getSize(Object object) {
		return ((Collection) object).size();
	}

	public Object getItemAt(int index, Object sequence,
			RetrievalContext retrievalContext, String contextHint) {
		return new UnsupportedOperationException();
	}

	public Object nextItem(int index, Object lastVal, Object sequence)
			throws ClassCastException {
		if (lastVal == null) {
			Iterator i = ((Collection) sequence).iterator();
			Object rtn = i.next();
			return new IteratorSequenceItem(rtn, i);
		} else {
			IteratorSequenceItem isi = (IteratorSequenceItem) lastVal;
			isi.setObject(isi.getIterator().next());
			return isi;
		}
	}

	public boolean hasNext(int index, Object previousItem, Object sequence) {
		if (null == previousItem)
			return ((Collection) sequence).size() > 0;
		else
			return ((IteratorSequenceItem) previousItem).getIterator()
					.hasNext();
	}

	public int indexOf(Object object, Object sequence) {
		int index = 0;
		for (Iterator i = ((Collection) sequence).iterator(); i.hasNext(); index++) {
			if (i.next().equals(object))
				return index;
		}
		return -1;
	}

	public int lastIndexOf(Object object, Object sequence)
			throws ClassCastException {
		int rtnIndex = -1;
		int index = 0;
		for (Iterator i = ((Collection) sequence).iterator(); i.hasNext(); index++) {
			if (i.next().equals(object))
				rtnIndex = index;
		}
		return rtnIndex;
	}

	public void setItemAt(int index, Object value, Object sequence) {
		throw new UnsupportedOperationException();
	}

	public void addItemAt(int index, Object value, Object sequence) {
		throw new UnsupportedOperationException();
	}

	public boolean contains(Object obj, Object sequence)
			throws ClassCastException {
		return ((Collection) sequence).contains(obj);
	}
}
