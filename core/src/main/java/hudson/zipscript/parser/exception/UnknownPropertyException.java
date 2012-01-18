/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.exception;

public class UnknownPropertyException extends ExecutionException {
	private static final long serialVersionUID = -1363961609103512907L;

	private String propertyName;
	private Object caller;

	public UnknownPropertyException(String propertyName, Object caller) {
		super(propertyName + " on " + caller.getClass().getName(), null);
		this.propertyName = propertyName;
		this.caller = caller;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public Object getCaller() {
		return caller;
	}

	public void setCaller(Object caller) {
		this.caller = caller;
	}
}