package com.tusaryan.SpringSecurityApp.SecurityApplication;

import com.tusaryan.SpringSecurityApp.SecurityApplication.entities.User;
import com.tusaryan.SpringSecurityApp.SecurityApplication.services.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SecurityApplicationTests {

	//import jwt service
	@Autowired
	private JwtService jwtService;

	@Test
	void contextLoads() {

		//creating a dummy user
		User user = new User(4L, "aryan@gmail.com", "1234");

		//generate a token with the algorithm that it uses
		String token = jwtService.generateToken(user);
		System.out.println(token);

		//get userId from token
//		Long id = jwtService.getUserIdFromToken(token);

		//To show it throws exceptions, if we pass wrong token/if token is expired if we pass it again after one min according to our code/if someone mess with the token
		Long id = jwtService.getUserIdFromToken("eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiI0IiwiZW1haWwiOiJhcnlhbkBnbWFpbC5jb20iLCJyb2xlcyI6WyJBRE1JTiIsIlVTRVIiXSwiaWF0IjoxNzI5MTcxOTc3LCJleHAiOjE3MjkxNzIwMzd9.f5Vb-LUFMVL9-EREahMP_O1lUex2LXNKSbbmDHc4S4R5v-uCz6SyMrgiiFmrXQHn");
		System.out.println(id);

	}

}
