package hudson.zipscript.ext.data;

import hudson.zipscript.parser.template.element.Element;

public class DefaultElementContainer {

	public Element defaultElement;
	public Element nextElement;

	public DefaultElementContainer (
			Element defaultElement, Element nextElement) {
		this.defaultElement = defaultElement;
		this.nextElement = nextElement;
	}
}
