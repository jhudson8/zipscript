/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.util.translator;

import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.lang.TextElement;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTranslator implements Translator {

	protected List convertToElements(String translatedText, List elements) {
		List rtnElements = new ArrayList();
		rtnElements.add(translatedText);
		for (int i = 0; i < elements.size(); i++) {
			String s = getElementToken(i, (Element) elements.get(i));
			replaceToken(s, (Element) elements.get(i), rtnElements);
		}
		// now replace all text with TextElements
		for (int i = 0; i < rtnElements.size(); i++) {
			Object obj = rtnElements.get(i);
			if (obj instanceof String) {
				rtnElements.remove(i);
				rtnElements.add(i, new TextElement((String) obj));
			}
		}

		return rtnElements;
	}

	protected void replaceToken(String token, Element element,
			List elementsOrText) {
		for (int i = 0; i < elementsOrText.size(); i++) {
			if (elementsOrText.get(i) instanceof String) {
				String s = (String) elementsOrText.get(i);
				int index = s.indexOf(token);
				if (index == 0) {
					String postText = s.substring(index + token.length());
					elementsOrText.remove(i);
					elementsOrText.add(i, element);
					if (postText.length() > 0)
						elementsOrText.add(i + 1, postText);
					return;
				} else if (index > 0) {
					elementsOrText.remove(i);
					String preText = s.substring(0, index);
					elementsOrText.add(i, preText);
					elementsOrText.add(i + 1, element);
					String postText = s.substring(index + token.length());
					if (postText.length() > 0)
						elementsOrText.add(i + 2, postText);
					return;
				}
			}
		}
		throw new ExecutionException(
				"Translator exception: result variable token '" + token
						+ "' could not be found", null);
	}

	protected abstract String getElementToken(int i, Element element);
}
