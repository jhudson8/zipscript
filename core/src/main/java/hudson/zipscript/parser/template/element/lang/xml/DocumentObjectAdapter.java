/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class DocumentObjectAdapter extends NodeObjectAdapter {

	public static DocumentObjectAdapter INSTANCE = new DocumentObjectAdapter();

	public boolean appliesTo(Object object) {
		return (object instanceof Document);
	}

	protected Node getNode(Object object) {
		return ((Document) object).getDocumentElement();
	}
}