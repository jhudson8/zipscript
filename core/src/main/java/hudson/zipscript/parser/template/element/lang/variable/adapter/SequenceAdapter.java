/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable.adapter;

public interface SequenceAdapter {

	public boolean appliesTo(Object object);

	public int getSize(Object sequence) throws ClassCastException;

	public Object nextItem(int index, Object lastVal, Object sequence)
			throws ClassCastException;

	public boolean hasNext(int index, Object previousItem, Object sequence);

	public Object getItemAt(int index, Object sequence,
			RetrievalContext retrievalContext, String contextHint)
			throws ClassCastException;

	public void setItemAt(int index, Object value, Object sequence)
			throws ClassCastException;

	public void addItemAt(int index, Object value, Object sequence)
			throws ClassCastException;

	public int indexOf(Object object, Object sequence)
			throws ClassCastException;

	public int lastIndexOf(Object object, Object sequence)
			throws ClassCastException;

	public boolean contains(Object obj, Object sequence)
			throws ClassCastException;
}
