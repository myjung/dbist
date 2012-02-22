/**
 * Copyright 2011 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.dbist;

import java.io.StringWriter;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

/**
 * @author Steve Jung
 */
public class DbistTest {
	@BeforeClass
	public static final void beforClass() {
		Velocity.init();
	}

	@Test
	public final void test() throws Exception {
		StringWriter writer1 = new StringWriter();
		VelocityContext vc = new VelocityContext();
		vc.put("name", "World");
		Velocity.evaluate(vc, writer1, "test", "Hello $name! Welcome to Velocity!");

		StringWriter writer2 = new StringWriter();
		vc = new VelocityContext();
		Velocity.evaluate(vc, writer2, "test", "Hello World! Welcome to Velocity!");

		Assert.assertEquals(writer1.toString(), writer2.toString());
	}

	@Test
	public final void testQuery() throws Exception {
		String query = FileUtils.readFileToString(ResourceUtils.getFile("classpath:com/googlecode/dbist/query.vm"));

		StringWriter writer = new StringWriter();
		VelocityContext vc = new VelocityContext();
		vc.put("name", "junguitar");
		vc.put("name1", "junguitar");
		Velocity.evaluate(vc, writer, query, query);

		System.out.println(writer);
	}
}