/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.directive.setdir;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ElementIndex;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.AbstractElement;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.ElementAttribute;
import hudson.zipscript.parser.template.element.group.MapElement;
import hudson.zipscript.parser.util.AttributeUtil;

import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NewMapElement extends AbstractElement implements Element {

	List attributes = null;

	public NewMapElement(MapElement me, ParsingSession session,
			Element setElement) throws ParseException {
		List children = me.getChildren();
		if (null != children && children.size() > 0) {
			attributes = new ArrayList();
			while (children.size() > 0) {
				ElementAttribute ea = AttributeUtil.getNamedAttribute(children,
						session, setElement);
				attributes.add(ea);
			}
		}
	}

	public boolean booleanValue(ExtendedContext context)
			throws ExecutionException {
		return false;
	}

	public List getChildren() {
		return null;
	}

	public void merge(ExtendedContext context, Writer sw)
			throws ExecutionException {
	}

	public ElementIndex normalize(int index, List elementList,
			ParsingSession session) throws ParseException {
		return null;
	}

	public Object objectValue(ExtendedContext context)
			throws ExecutionException {
		Map rtn = new HashMap();
		if (null != attributes) {
			for (Iterator i = attributes.iterator(); i.hasNext();) {
				ElementAttribute ea = (ElementAttribute) i.next();
				Object val = ea.getValue().objectValue(context);
				if (null != val)
					rtn.put(ea.getName(), val);
			}
		}
		return rtn;
	}
}