/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.xml;

import hudson.zipscript.parser.template.element.lang.variable.adapter.ObjectAdapter;
import hudson.zipscript.parser.template.element.lang.variable.adapter.RetrievalContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeObjectAdapter implements ObjectAdapter {

	public static NodeObjectAdapter INSTANCE = new NodeObjectAdapter();

	public boolean appliesTo(Object object) {
		return (object instanceof Node);
	}

	public Object call(String key, Object[] parameters, Object object)
			throws ClassCastException {
		throw new UnsupportedOperationException();
	}

	public Object get(String key, Object object,
			RetrievalContext retrievalContext, String contextHint)
			throws ClassCastException {
		Node node = getNode(object);
		if (retrievalContext.is(RetrievalContext.SEQUENCE)) {
			// find node children whose name match the key
			NodeList nl = node.getChildNodes();
			List matchingChildren = new ArrayList();
			for (int i = 0; i < nl.getLength(); i++) {
				if (nl.item(i).getNodeName().equals(key))
					matchingChildren.add(nl.item(i));
			}
			if (matchingChildren.size() == 1 && null != contextHint) {
				// check for a wrapped list
				boolean matchFound = false;
				nl = ((Node) matchingChildren.get(0)).getChildNodes();
				for (int i = 0; i < nl.getLength(); i++) {
					if (nl.item(i).getNodeName().equals(contextHint)) {
						if (!matchFound) {
							matchingChildren.clear();
							matchFound = true;
						}
						matchingChildren.add(nl.item(i));
					}
				}
			}
			return matchingChildren;
		} else if (retrievalContext.is(RetrievalContext.HASH)) {
			NodeList nl = node.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				if (nl.item(i).getNodeName().equals(key))
					return nl.item(i);
			}
			return null;
		} else {
			Node attr = node.getAttributes().getNamedItem(key);
			if (null != attr)
				return attr.getNodeValue();
			else {
				// maybe a subnode attribute?
				NodeList nl = node.getChildNodes();
				for (int i = 0; i < nl.getLength(); i++) {
					if (nl.item(i).getNodeName().equals(key)) {
						nl = nl.item(i).getChildNodes();
						for (int j = 0; j < nl.getLength(); j++) {
							if (nl.item(j).getNodeType() == Node.TEXT_NODE)
								return nl.item(j).getNodeValue().trim();
						}
						return null;
					}
				}
				return null;
			}
		}
	}

	public Iterator getProperties(Object object) throws ClassCastException {
		throw new UnsupportedOperationException();
	}

	public void set(String key, Object value, Object object)
			throws ClassCastException {
		throw new UnsupportedOperationException();
	}

	protected Node getNode(Object object) {
		return (Node) object;
	}
}