/*
 * Copyright 2014 Cisco Systems, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.cisco.oss.foundation.configuration;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.expression.StandardBeanExpressionResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.MethodExecutor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.support.ReflectiveMethodResolver;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.List;

public class FoundationConfigurationFileNameResolver implements BeanFactoryAware {


	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		if (beanFactory instanceof ConfigurableBeanFactory) {
			ConfigurableBeanFactory cbf = (ConfigurableBeanFactory) beanFactory;
			cbf.setBeanExpressionResolver(new StandardBeanExpressionResolver() {
				@Override
				protected void customizeEvaluationContext(StandardEvaluationContext evalContext) {
					evalContext.addMethodResolver(new CabConfigurationReflectiveMethodResolver());
				}
			});
		}

	}

	public static String resolveConfigFileName() {
		String processName=System.getProperty("app.instance.name");
		
		if(StringUtils.isNotBlank(processName)){
			return "classpath:config." + processName + ".properties";
		}else{
			return "classpath:config.properties";
		}
	}


	private class CabConfigurationReflectiveMethodResolver extends ReflectiveMethodResolver {

		@Override
		public MethodExecutor resolve(EvaluationContext context, Object targetObject, String name, List<TypeDescriptor> argumentTypes) throws AccessException {

			if ("resolveConfigFileName".equals(name)) {
				return new CabConfigurationMethodExecutor();
			}
			return super.resolve(context, targetObject, name, argumentTypes);
		}

	}

	private class CabConfigurationMethodExecutor implements MethodExecutor {

		@Override
		public TypedValue execute(EvaluationContext context, Object target, Object... arguments) throws AccessException {

			try {
				return new TypedValue(resolveConfigFileName(), new TypeDescriptor(new MethodParameter(FoundationConfigurationFileNameResolver.class.getDeclaredMethod("resolveConfigFileName", new Class[] { }), -1)));
			} catch (Exception ex) {
				throw new AccessException("Problem invoking method: resolveConfigFileName", ex);
			}

		}

	}

}
