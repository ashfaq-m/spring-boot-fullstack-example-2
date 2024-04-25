package com.amigoscode;

import com.amigoscode.customer.Customer;
import com.amigoscode.customer.CustomerRepository;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@SpringBootApplication
//@ComponentScan(basePackages = "com.amigoscode")
//@EnableAutoConfiguration
//@Configuration
public class SpringBootExampleApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext applicationContext = SpringApplication.run(SpringBootExampleApplication.class, args);
		//printBeans(applicationContext);
	}


	@Bean("foo")
	public foo getFoo() {
		return new foo("bar");
	}


	public record foo(String name){

	}

	private static void printBeans(ConfigurableApplicationContext configurableApplicationContext){

		String[] beans =  configurableApplicationContext.getBeanDefinitionNames();

		for (String bean : beans) {
			System.out.println(bean);
		}
	}


	@Bean
	CommandLineRunner runner(CustomerRepository customerRepository){
		return args -> {
			Faker faker = new Faker();
			Random random  = new Random();
			Customer customer = new Customer(
					faker.name().fullName(),
					faker.internet().safeEmailAddress(),
					random.nextInt(16,99)
			);

			 customerRepository.save(customer);
		};
	}

}
