package com.shanghai;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author YeJR
 * @EnableRabbit 开启RabbitMQ支持
 * @ImportResource 导入xml文件
 * @ServletComponentScan 为了扫描在common文件夹下定义的filter
 * @EnableTransactionManagement 开启事物
 */
// [注]:引入rabbitMQ,引入事务注解,引入@ServletComponentScan注解后，Servlet、Filter、Listener可以直接通过@WebServlet、@WebFilter、@WebListener注解自动注册，无需其他代码
// [注]:可能是不需要类似x-springboot里的FilterConfig?最后引入druid配置
@EnableRabbit
@SpringBootApplication
@EnableTransactionManagement
@ServletComponentScan(basePackages="com.shanghai.*")
@ImportResource(locations = { "classpath:druid-bean.xml" })
public class MySpringBootApplication {

	// 访问路径：http://localhost:8071	初始账号密码：admin 123456
	public static void main(String[] args) {
		SpringApplication.run(MySpringBootApplication.class, args);
	}
}
