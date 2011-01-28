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

package org.springframework.transaction.annotation;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Feature;
import org.springframework.context.annotation.FeatureConfiguration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CallCountingTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.AnnotationTransactionNamespaceHandlerTests.TransactionalTestBean;
import org.springframework.transaction.config.TxAnnotationDriven;

/**
 * Integration tests for {@link TxAnnotationDriven} support within @Configuration
 * classes. Adapted from original tx: namespace tests at
 * {@link AnnotationTransactionNamespaceHandlerTests}.
 *
 * @author Chris Beams
 * @since 3.1
 */
public class TxAnnotationDrivenFeatureTests {
	@Test
	public void transactionProxyIsCreated() {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(TxFeatures.class, TxManagerConfig.class);
		ctx.refresh();
		TransactionalTestBean bean = ctx.getBean(TransactionalTestBean.class);
		assertThat("testBean is not a proxy", AopUtils.isAopProxy(bean), is(true));
		Map<?,?> services = ctx.getBeansWithAnnotation(Service.class);
		assertThat("Stereotype annotation not visible", services.containsKey("testBean"), is(true));
	}

	@Test
	public void txManagerIsResolvedOnInvocationOfTransactionalMethod() {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(TxFeatures.class, TxManagerConfig.class);
		ctx.refresh();
		TransactionalTestBean bean = ctx.getBean(TransactionalTestBean.class);

		// invoke a transactional method, causing the PlatformTransactionManager bean to be resolved.
		bean.findAllFoos();
	}

	@Test
	public void txManagerIsResolvedCorrectlyWhenMultipleManagersArePresent() {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(TxFeatures.class, MultiTxManagerConfig.class);
		ctx.refresh();
		TransactionalTestBean bean = ctx.getBean(TransactionalTestBean.class);

		// invoke a transactional method, causing the PlatformTransactionManager bean to be resolved.
		bean.findAllFoos();
	}

}

@FeatureConfiguration
class TxFeatures {

	@Feature
	public TxAnnotationDriven tx(TxManagerConfig txManagerConfig) {
		return new TxAnnotationDriven(txManagerConfig.txManager()).proxyTargetClass(false);
	}

}
@Configuration
class TxManagerConfig {

	@Bean
	public TransactionalTestBean testBean() {
		return new TransactionalTestBean();
	}

	@Bean
	public PlatformTransactionManager txManager() {
		return new CallCountingTransactionManager();
	}

}

@Configuration
class MultiTxManagerConfig extends TxManagerConfig {

	@Bean
	public PlatformTransactionManager txManager2() {
		return new CallCountingTransactionManager();
	}

}
