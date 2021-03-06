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

package org.springframework.web.method.support;

import java.util.Map;

import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.support.BindingAwareModelMap;

/**
 * Records model and view related decisions made by 
 * {@link HandlerMethodArgumentResolver}s and 
 * {@link HandlerMethodReturnValueHandler}s during the course of invocation of 
 * a controller method.
 * 
 * <p>The {@link #setResolveView(boolean)} flag can be used to indicate that
 * view resolution is not required (e.g. {@code @ResponseBody} method).
 * 
 * <p>A default {@link Model} is created at instantiation and used thereafter. 
 * The {@link #setRedirectModel(ModelMap)} method can be used to provide a 
 * separate model to use potentially in case of a redirect. 
 * The {@link #setUseRedirectModel()} can be used to enable use of the 
 * redirect model if the controller decides to redirect. 
 * 
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class ModelAndViewContainer {

	private Object view;
	
	private boolean resolveView = true;
	
	private final ModelMap model = new BindingAwareModelMap();

	private ModelMap redirectModel;

	private boolean useRedirectModel = false;

	/**
	 * Create a new instance.
	 */
	public ModelAndViewContainer() {
	}

	/**
	 * Set a view name to be resolved by the DispatcherServlet via a ViewResolver. 
	 * Will override any pre-existing view name or View.
	 */
	public void setViewName(String viewName) {
		this.view = viewName;
	}

	/**
	 * Return the view name to be resolved by the DispatcherServlet via a 
	 * ViewResolver, or {@code null} if a View object is set.
	 */
	public String getViewName() {
		return (this.view instanceof String ? (String) this.view : null);
	}
	
	/**
	 * Set a View object to be used by the DispatcherServlet. 
	 * Will override any pre-existing view name or View.
	 */
	public void setView(Object view) {
		this.view = view;
	}

	/**
	 * Return the View object, or {@code null} if we using a view name
	 * to be resolved by the DispatcherServlet via a ViewResolver.
	 */
	public Object getView() {
		return this.view;
	}

	/**
	 * Whether the view is a view reference specified via a name to be 
	 * resolved by the DispatcherServlet via a ViewResolver.
	 */
	public boolean isViewReference() {
		return (this.view instanceof String);
	}
	
	/**
	 * Whether view resolution is required or not. 
	 * <p>A {@link HandlerMethodReturnValueHandler} may use this flag to 
	 * indicate the response has been fully handled and view resolution 
	 * is not required (e.g. {@code @ResponseBody}).
	 * <p>A {@link HandlerMethodArgumentResolver} may also use this flag
	 * to indicate the presence of an argument (e.g. 
	 * {@code ServletResponse} or {@code OutputStream}) that may lead to 
	 * a complete response depending on the method return value.
	 * <p>The default value is {@code true}.
	 */
	public void setResolveView(boolean resolveView) {
		this.resolveView = resolveView;
	}
	
	/**
	 * Whether view resolution is required or not.
	 */
	public boolean isResolveView() {
		return this.resolveView;
	}

	/**
	 * Return the default model created at instantiation or the one provided 
	 * via {@link #setRedirectModel(ModelMap)} as long as it has been enabled 
	 * via {@link #setUseRedirectModel()}.
	 */
	public ModelMap getModel() {
		if ((this.redirectModel != null) && this.useRedirectModel) {
			return this.redirectModel;
		}
		else {
			return this.model;
		}
	}
	
	/**
	 * Provide a model instance to use in case the controller redirects. 
	 * Note that {@link #setUseRedirectModel()} must also be called in order 
	 * to enable use of the redirect model.
	 */
	public void setRedirectModel(ModelMap redirectModel) {
		this.redirectModel = redirectModel;
	}

	/**
	 * Return the redirect model provided via 
	 * {@link #setRedirectModel(ModelMap)} or {@code null} if not provided.
	 */
	public ModelMap getRedirectModel() {
		return this.redirectModel;
	}

	/**
	 * Indicate that the redirect model provided via 
	 * {@link #setRedirectModel(ModelMap)} should be used.
	 */
	public void setUseRedirectModel() {
		this.useRedirectModel = true;
	}

	/**
	 * Add the supplied attribute to the underlying model.
	 * @see ModelMap#addAttribute(String, Object)
	 */
	public ModelAndViewContainer addAttribute(String name, Object value) {
		getModel().addAttribute(name, value);
		return this;
	}
	
	/**
	 * Add the supplied attribute to the underlying model.
	 * @see Model#addAttribute(Object)
	 */
	public ModelAndViewContainer addAttribute(Object value) {
		getModel().addAttribute(value);
		return this;
	}

	/**
	 * Copy all attributes to the underlying model.
	 * @see ModelMap#addAllAttributes(Map)
	 */
	public ModelAndViewContainer addAllAttributes(Map<String, ?> attributes) {
		getModel().addAllAttributes(attributes);
		return this;
	}

	/**
	 * Copy attributes in the supplied <code>Map</code> with existing objects of 
	 * the same name taking precedence (i.e. not getting replaced).
	 * @see ModelMap#mergeAttributes(Map)
	 */
	public ModelAndViewContainer mergeAttributes(Map<String, ?> attributes) {
		getModel().mergeAttributes(attributes);
		return this;
	}

	/**
	 * Whether the underlying model contains the given attribute name.
	 * @see ModelMap#containsAttribute(String)
	 */
	public boolean containsAttribute(String name) {
		return getModel().containsAttribute(name);
	}

	/**
	 * Return diagnostic information.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("ModelAndViewContainer: ");
		if (isResolveView()) {
			if (isViewReference()) {
				sb.append("reference to view with name '").append(this.view).append("'");
			}
			else {
				sb.append("View is [").append(this.view).append(']');
			}
			sb.append("; model is ").append(getModel());
		}
		else {
			sb.append("View resolution not required");
		}
		return sb.toString();
	}
	
}
