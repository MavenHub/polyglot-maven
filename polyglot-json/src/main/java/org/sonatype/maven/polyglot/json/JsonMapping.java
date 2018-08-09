/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.json;

import java.util.Map;

import org.codehaus.plexus.component.annotations.Component;
import org.sonatype.maven.polyglot.mapping.Mapping;
import org.sonatype.maven.polyglot.mapping.MappingSupport;

/**
 * XML model mapping.
 *
 */
@Component(role = Mapping.class, hint = "json")
public class JsonMapping extends MappingSupport {

	public JsonMapping() {
		super("json");
		setPomNames("pom.json");
		setAcceptLocationExtensions(".json");
		setAcceptOptionKeys("json:4.0.0");
		setPriority(-1);
	}

	@Override
	public boolean accept(Map<String, ?> options) {
		if (options != null) {

			String location = getLocation(options);
			if (location != null) {
				if (location.endsWith(".json")) {
					return true;
				}
			}
		}

		return false;
	}
}