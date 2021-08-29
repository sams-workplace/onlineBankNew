package com.example.template;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.template.entity.User;
import com.example.template.repository.UserRepository;


@SpringBootApplication
public class AuthorizationServerApplication implements CommandLineRunner {

	public static void main( String[] args )
	{
		SpringApplication.run(AuthorizationServerApplication.class, args);
	}

	@Autowired
	private UserRepository repository;
	@Autowired private PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) throws Exception {

		User user = new User();
		user.setUsername("1@sk.com");
		user.setPassword(passwordEncoder.encode("1234"));
		user.setNickName("유은상");
		user.setAddress("서울시");
		user.setRole("USER_ADMIN");
		repository.save(user);

	}


	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
