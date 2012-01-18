/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.resource;

import hudson.zipscript.parser.exception.ExecutionException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileResource extends AbstractResource {

	File file;
	long lastModified;

	public FileResource(File file) {
		this.file = file;
	}

	public InputStream getInputStream() {
		try {
			lastModified = file.lastModified();
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new ExecutionException("The file '" + file.getPath()
					+ "' could not be located", null, e);
		}
	}

	public boolean hasBeenModified() {
		return (file.lastModified() > lastModified);
	}
}
