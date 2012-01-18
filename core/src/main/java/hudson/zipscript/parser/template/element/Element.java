/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ElementIndex;
import hudson.zipscript.parser.template.data.ParsingSession;

import java.io.Writer;
import java.util.List;

public interface Element {

	/**
	 * Set the absolute start position index for this element within it's
	 * containg resource
	 * 
	 * @param position
	 */
	public void setElementPosition(long position);

	/**
	 * Return the absolute element start position
	 */
	public long getElementPosition();

	/**
	 * Set the number of characters used by this element
	 * 
	 * @param length
	 *            the length
	 */
	public void setElementLength(int length);

	/**
	 * Return the number of characters used by this element
	 * 
	 * @return
	 */
	public int getElementLength();

	/**
	 * Return the object value reference by this element and it's nested
	 * structure
	 * 
	 * @param context
	 *            the data context
	 * @throws ExecutionException
	 */
	public Object objectValue(ExtendedContext context)
			throws ExecutionException;

	/**
	 * Return a boolean value referenced by this element and it's nested
	 * structure
	 * 
	 * @param context
	 *            the data context
	 * @throws ExecutionException
	 */
	public boolean booleanValue(ExtendedContext context)
			throws ExecutionException;

	/**
	 * Merge this element and it's nested structure into the provided writer
	 * 
	 * @param context
	 *            the data context
	 * @param sw
	 *            the Writer
	 * @throws ExecutionException
	 */
	public void merge(ExtendedContext context, Writer sw)
			throws ExecutionException;

	/**
	 * Allow this element to evaluate the parsed document structure to
	 * self-contain all nested elements if applicable
	 * 
	 * @param index
	 *            the starting index in the element list (minus this element)
	 * @param elementList
	 *            the document element list (minus this element)
	 * @param session
	 *            the parsing session
	 * @return null if this element is to be added back to this list and an
	 *         ElementIndex for more complex behavior
	 * @throws ParseException
	 */
	public ElementIndex normalize(int index, List elementList,
			ParsingSession session) throws ParseException;

	/**
	 * Return all children of this element or null if N/A
	 */
	public List getChildren();

	/**
	 * This is called after full parsing and normalization for any elements that
	 * might need to lazy load. it is not required to call this on children as
	 * long as the getChildren method return all children.
	 * 
	 * @param session
	 * @throws ParseException
	 */
	public void validate(ParsingSession session) throws ParseException;
}
