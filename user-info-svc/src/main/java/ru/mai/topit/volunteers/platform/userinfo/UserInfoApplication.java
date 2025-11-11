package ru.mai.topit.volunteers.platform.userinfo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class UserInfoApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserInfoApplication.class, args);
	}

}
