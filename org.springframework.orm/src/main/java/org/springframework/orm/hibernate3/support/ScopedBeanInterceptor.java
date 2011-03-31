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

package org.springframework.orm.hibernate3.support;

import org.hibernate.EmptyInterceptor;

import org.springframework.aop.scope.ScopedObject;
import org.springframework.aop.support.AopUtils;

/**
 * Hibernate3 interceptor used for getting the proper entity name for scoped
 * beans. As scoped bean classes are proxies generated at runtime, they are
 * unrecognized by the persisting framework. Using this interceptor, the
 * original scoped bean class is retrieved end exposed to Hibernate for
 * persisting.
 *
 * <p>Usage example:
 *
 * <pre class="code">
 * {@code
 * <bean id="sessionFactory" class="org.springframework.orm.hibernate3.LocalSessionFactoryBean">
 *   ...
 *   <property name="entityInterceptor">
 *     <bean class="org.springframework.orm.hibernate3.support.ScopedBeanInterceptor"/>
 *   </property>
 * </bean>}</pre>
 *
 * @author Costin Leau
 * @author Juergen Hoeller
 * @since 2.0
 */
@SuppressWarnings("serial")
public class ScopedBeanInterceptor extends EmptyInterceptor {

	@Override
	public String getEntityName(Object entity) {
		if (entity instanceof ScopedObject) {
			// Determine underlying object's type.
			Object targetObject = ((ScopedObject) entity).getTargetObject();
			return AopUtils.getTargetClass(targetObject).getName();
		}

		// Any other object: delegate to the default implementation.
		return super.getEntityName(entity);
	}

}
