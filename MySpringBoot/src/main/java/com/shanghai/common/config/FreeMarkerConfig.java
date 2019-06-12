package com.shanghai.common.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.shanghai.common.tag.MenuTag;

/**
* @author: YeJR 
* @version: 2018年5月31日 上午11:41:28
*/
@Configuration
public class FreeMarkerConfig {
	
	@Autowired
    protected freemarker.template.Configuration configuration;
	
	@Autowired
    protected MenuTag menuTag;
	
	/**
	 * 自定义标签
	 */
	@PostConstruct
    public void setSharedVariable() {
		// [注]:这里注解先在Spring初始化的时候加载配置,然后配置加载html的资源路径,最后在html里可以用FreeMaker的对象了${ctx}
		// [注]:这里估计是把menu作为全局引用的
        configuration.setSharedVariable("menu", menuTag);
    }

}
