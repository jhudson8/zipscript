/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable.adapter;

public class ObjectArrayAdapter implements SequenceAdapter {

	public static ObjectArrayAdapter INSTANCE = new ObjectArrayAdapter();

	public boolean appliesTo(Object object) {
		return (object instanceof Object[]);
	}

	public int getSize(Object object) {
		return ((Object[]) object).length;
	}

	public Object getItemAt(int index, Object sequence,
			RetrievalContext retrievalContext, String contextHint) {
		return ((Object[]) sequence)[index];
	}

	public boolean contains(Object object, Object sequence) {
		Object[] arr = (Object[]) sequence;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i].equals(object))
				return true;
		}
		return false;
	}

	public void setItemAt(int index, Object value, Object sequence)
			throws ClassCastException {
		((Object[]) sequence)[index] = value;
	}

	public void addItemAt(int index, Object value, Object sequence)
			throws ClassCastException {
		throw new UnsupportedOperationException();
	}

	public boolean hasNext(int index, Object previousItem, Object sequence) {
		return ((Object[]) sequence).length > (index + 1);
	}

	public int indexOf(Object object, Object sequence)
			throws ClassCastException {
		Object[] arr = (Object[]) sequence;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i].equals(object))
				return i;
		}
		return -1;
	}

	public int lastIndexOf(Object object, Object sequence)
			throws ClassCastException {
		int rtnIndex = -1;
		Object[] arr = (Object[]) sequence;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i].equals(object))
				rtnIndex = i;
		}
		return rtnIndex;
	}

	public Object nextItem(int index, Object lastVal, Object sequence)
			throws ClassCastException {
		return ((Object[]) sequence)[index];
	}
}