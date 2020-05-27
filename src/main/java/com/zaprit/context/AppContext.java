/**
 * 
 */
package com.zaprit.context;

import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.stereotype.Component;

/**
 * @author vaibhav.singh
 */
@Component
public final class AppContext
{
	private static final Set<BeanFactory> beanFactories = new LinkedHashSet<>();

	private AppContext()
	{}

	/**
	 * @param beanFactory
	 */
	public static void addBeanFactory(BeanFactory beanFactory)
	{
		beanFactories.add(beanFactory);
	}

	/**
	 * @param name
	 * @return Object
	 */
	public static Object getBean(String name)
	{
		Object bean = null;
		for (BeanFactory beanFactory : beanFactories)
		{
			try
			{
				bean = beanFactory.getBean(name);
				break;
			}
			catch (NoSuchBeanDefinitionException e)
			{}
		}
		return bean;
	}

}
