/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.util;

import hudson.zipscript.ZipEngine;
import hudson.zipscript.resource.FileResourceLoader;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;

public class CommandLineUtil {

	public static void main(String[] args) {
		if (args.length != 1) {
			System.err
					.println("You must provide the filename as the first argument");
			System.exit(-1);
		}

		HashMap props = new HashMap();
		props.put("templateResourceLoader.class", FileResourceLoader.class
				.getName());
		ZipEngine engine = ZipEngine.createInstance(props);
		Writer fw = null;
		try {
			fw = new FileWriter(args[0] + ".txt");
			engine.getTemplate(args[0]).merge(null, fw);
			fw.close();
		} catch (Exception e) {
			if (null != fw)
				e.printStackTrace(new PrintWriter(fw));
			System.exit(-1);
		}
	}
}
