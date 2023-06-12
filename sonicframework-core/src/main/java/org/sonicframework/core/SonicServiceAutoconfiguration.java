package org.sonicframework.core;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"org.sonicframework.core"})
//@EnableAspectJAutoProxy(proxyTargetClass = true)
public class SonicServiceAutoconfiguration {

	@Autowired
	private ApplicationContext applicationContext;
	
	@PostConstruct
	public void postConstruct() {
		SpringApplicationContextUtil.setApplicationContext(applicationContext);
		
	}
	
}
