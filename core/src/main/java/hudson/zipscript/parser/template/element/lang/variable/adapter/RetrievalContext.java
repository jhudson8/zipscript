/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable.adapter;

public class RetrievalContext {

	public static final RetrievalContext UNKNOWN = new RetrievalContext(0);
	public static final RetrievalContext SEQUENCE = new RetrievalContext(1);
	public static final RetrievalContext HASH = new RetrievalContext(2);
	public static final RetrievalContext SCALAR = new RetrievalContext(3);
	public static final RetrievalContext BOOLEAN = new RetrievalContext(13);
	public static final RetrievalContext NUMBER = new RetrievalContext(23);
	public static final RetrievalContext TEXT = new RetrievalContext(33);

	private int type;

	private RetrievalContext(int type) {
		this.type = type;
	}

	public boolean is(RetrievalContext retrievalContext) {
		return (retrievalContext.type == 0 || retrievalContext.type == type || (retrievalContext.type % 10) == type);
	}
}
