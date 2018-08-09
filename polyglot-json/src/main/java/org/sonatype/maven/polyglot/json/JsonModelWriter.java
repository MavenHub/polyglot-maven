/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.json;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.ModelWriter;
import org.codehaus.plexus.component.annotations.Component;
import org.sonatype.maven.polyglot.io.ModelWriterSupport;
import org.sonatype.maven.polyglot.json.ModelToJsonWriter;

/**
 * Json model writer.
 *
 */
@Component(role = ModelWriter.class, hint = "json")
public class JsonModelWriter extends ModelWriterSupport {
	
	public void write(Writer output, Map<String, Object> o, Model model) throws IOException {
		
		ModelToJsonWriter toJavaWriter = new ModelToJsonWriter(output, model);
		toJavaWriter.write();
		output.flush();
	}
}
