package com.amigos.securitytestone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;;

@SpringBootApplication
public class SecurityTestOneApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityTestOneApplication.class, args);
	}

	// @Bean
	// public CommandLineRunner commandLineRunner(AuthenticationService service) {
	// return args -> {
	// var admin = RegisterRequest.builder()
	// .firstname("admin")
	// .lastname("admin")
	// .email("admin@gmail.com")
	// .password("admin")
	// .role(ADMIN)
	// .build();

	// var manager = RegisterRequest.builder()
	// .firstname("manager")
	// .lastname("manager")
	// .email("manager@gmail.com")
	// .password("manager")
	// .role(MANAGER)
	// .build();
	// };
	// }

}
