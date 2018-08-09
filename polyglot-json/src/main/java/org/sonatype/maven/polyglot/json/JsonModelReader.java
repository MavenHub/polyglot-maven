/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.ModelParseException;
import org.apache.maven.model.io.ModelReader;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;
import org.json.JSONObject;
import org.sonatype.maven.polyglot.io.ModelReaderSupport;

/**
 * Json model reader.
 *
 */
@Component(role = ModelReader.class, hint = "json")
public class JsonModelReader extends ModelReaderSupport {

	@Requirement
	private PlexusContainer container;

	@Requirement
	protected Logger log;


	@Override
	public Model read(Reader reader, Map<String, ?> options) throws IOException, ModelParseException {
	    JSONObject json = new JSONObject(readFile(reader));
	    JsonToModelConstructor modelConst = new JsonToModelConstructor(json);
	    return modelConst.construct();
	}

	private String readFile(Reader fileReader) {
		StringBuilder content = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(fileReader);
			String line;
			while ((line = reader.readLine()) != null) {
				content.append(line);
				content.append(System.lineSeparator());
			}
			reader.close();
			return content.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}