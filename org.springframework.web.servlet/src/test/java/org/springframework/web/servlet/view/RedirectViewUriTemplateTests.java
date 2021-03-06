/*
 * Copyright 2002-2011 the original author or authors.
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

package org.springframework.web.servlet.view;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.HandlerMapping;

public class RedirectViewUriTemplateTests {

	private MockHttpServletRequest request;

	private MockHttpServletResponse response;

	@Before
	public void setUp() {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
	}

	@Test
	public void uriTemplate() throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("foo", "bar");

		String baseUrl = "http://url.somewhere.com";
		RedirectView redirectView = new RedirectView(baseUrl + "/{foo}");
		redirectView.renderMergedOutputModel(model, request, response);

		assertEquals(baseUrl + "/bar", response.getRedirectedUrl());
	}
	
	@Test
	public void uriTemplateEncode() throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("foo", "bar/bar baz");

		String baseUrl = "http://url.somewhere.com";
		RedirectView redirectView = new RedirectView(baseUrl + "/context path/{foo}");
		redirectView.renderMergedOutputModel(model, request, response);

		assertEquals(baseUrl + "/context path/bar%2Fbar%20baz", response.getRedirectedUrl());
	}
	
	@Test
	public void uriTemplateAndArrayQueryParam() throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("foo", "bar");
		model.put("fooArr", new String[] { "baz", "bazz" });

		RedirectView redirectView = new RedirectView("/foo/{foo}");
		redirectView.renderMergedOutputModel(model, request, response);

		assertEquals("/foo/bar?fooArr=baz&fooArr=bazz", response.getRedirectedUrl());
	}

	@Test
	public void uriTemplateWithObjectConversion() throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("foo", new Long(611));

		RedirectView redirectView = new RedirectView("/foo/{foo}");
		redirectView.renderMergedOutputModel(model, request, response);

		assertEquals("/foo/611", response.getRedirectedUrl());
	}

	@Test
	public void uriTemplateReuseCurrentRequestVars() throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("key1", "value1");
		model.put("name", "value2");
		model.put("key3", "value3");
		
		Map<String, String> currentRequestUriTemplateVars = new HashMap<String, String>();
		currentRequestUriTemplateVars.put("var1", "v1");
		currentRequestUriTemplateVars.put("name", "v2");
		currentRequestUriTemplateVars.put("var3", "v3");
		request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, currentRequestUriTemplateVars);

		String url = "http://url.somewhere.com";
		RedirectView redirectView = new RedirectView(url + "/{key1}/{var1}/{name}");
		redirectView.renderMergedOutputModel(model, request, response);

		assertEquals(url + "/value1/v1/value2?key3=value3", response.getRedirectedUrl());
	}

	@Test(expected=IllegalArgumentException.class)
	public void uriTemplateNullValue() throws Exception {
		new RedirectView("/{foo}").renderMergedOutputModel(new ModelMap(), request, response);
	}
	
	@Test
	public void emptyRedirectString() throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();

		RedirectView redirectView = new RedirectView("");
		redirectView.renderMergedOutputModel(model, request, response);

		assertEquals("", response.getRedirectedUrl());
	}
	
}
