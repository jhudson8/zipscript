/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable;

import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ElementIndex;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.group.GroupElement;
import hudson.zipscript.parser.template.element.group.MapElement;
import hudson.zipscript.parser.template.element.lang.DotElement;
import hudson.zipscript.parser.template.element.special.SpecialElement;
import hudson.zipscript.parser.template.element.special.SpecialStringElement;
import hudson.zipscript.parser.template.element.special.SpecialStringElementImpl;

import java.util.ArrayList;
import java.util.List;

public class SpecialVariableElementImpl extends VariableElement implements
		SpecialStringElement {
	
	private String root;
	private boolean suckInHashExpressions;

	// normally we won't do this but there are occasions with variable defaults
	private boolean shouldEvaluateSeparators = true;

	public SpecialVariableElementImpl(String text, ParsingSession session,
			int contentPosition) throws ParseException {
		this.root = text;
		setPattern(text, session, contentPosition);
		this.suckInHashExpressions = true;
	}

	public SpecialVariableElementImpl(String text, ParsingSession session,
			int contentPosition, boolean suckInHashExpressions) throws ParseException {
		this.root = text;
		this.suckInHashExpressions = suckInHashExpressions;
		setPattern(text, session, contentPosition);
	}

	/**
	 * Create the pattern text and have the VariableElement manage parsing the
	 * pattern
	 */
	public ElementIndex normalize(int index, List elementList,
			ParsingSession session) throws ParseException {
		List elements = new ArrayList();
		elements.add(new SpecialStringElementImpl(root));
		
		StringBuffer pattern = null;
		while (elementList.size() > index) {
			Element e = (Element) elementList.get(index);
			if (e instanceof SpecialElement) {
				elementList.remove(index);
				elements.add(e);
				continue;
			} else if (e instanceof VariableTokenSeparatorElement) {
				elementList.remove(index);
				e.normalize(index, elementList, session);
				addSpecialElement((VariableTokenSeparatorElement) e);
			} else if (isShouldEvaluateSeparators()
					&& e instanceof GroupElement) {
				elementList.remove(index);
				e.normalize(index, elementList, session);
				elements.add(e);
			} else if (isShouldEvaluateSeparators()
					&& e instanceof MapElement && suckInHashExpressions) {
				elementList.remove(index);
				e.normalize(index, elementList, session);
				elements.add(e);
			} else if (isShouldEvaluateSeparators() && e instanceof DotElement) {
				elementList.remove(index);
				elements.add(e);
				if (elementList.size() > index) {
					e = (Element) elementList.remove(index);
					if (e instanceof SpecialElement) {
						elements.add(e);
						continue;
					} else if (e instanceof SpecialVariableElementImpl) {
						elements.add(e);
						continue;
					} else {
						throw new ParseException(this,
								"Invalid variable sequence '" + pattern + "'");
					}
				} else {
					throw new ParseException(this,
							"Invalid variable sequence '" + pattern + "'");
				}

			} else {
				break;
			}
		}
		this.children = parse(elements, session);
		return null;
	}
	
	public String getPropertyName() {
		return (children[0].getPropertyName());
	}
	
	public String getTokenValue() {
		return getPattern();
	}

	public MapElement getLastMapElement() {
		if (children.length > 0) {
			if (children[children.length-1] instanceof MapChild) {
				return ((MapChild) children[children.length-1]).getMapElement();
			}
		}
		return null;
	}

	public boolean isShouldEvaluateSeparators() {
		return shouldEvaluateSeparators;
	}

	public void setShouldEvaluateSeparators(boolean shouldEvaluateSeparators) {
		this.shouldEvaluateSeparators = shouldEvaluateSeparators;
	}

	public String getStartToken() {
		return "${";
	}

	public String getEndToken() {
		return "}";
	}
}