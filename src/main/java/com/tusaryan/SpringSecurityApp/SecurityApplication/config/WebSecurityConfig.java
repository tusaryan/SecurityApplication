package com.tusaryan.SpringSecurityApp.SecurityApplication.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

//Configuring SecurityFilterChain to customise default behaviour
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    //if we define web security like this than we have to define formLogin else it will not work.
    //since all the request have to go through the filter chain. So any incoming request will also have to pass through security filter chain
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        //to configure filter chain
        httpSecurity

                //each of the request have to be authenticated before they move further in the filter chain
                //to ask for authorization and pass auth i.e. login id and pass, inside
                .authorizeHttpRequests(auth -> auth

                        //to whitelist some routes i.e. to make those routes public and anyone can go to those routes.
                        //inside () pass the pattern of request. for eg: pass in http methods like all get request are public or pass the type of url.
                        .requestMatchers("/posts", "/error", "/public/**").permitAll()

                        //to authorize your role(here specifically admin) if you want to access /posts and any route inside it like find posts by id.
                        //here only admins are allowed to go to these routes /posts/id
                        .requestMatchers("/posts/**").hasAnyRole("ADMIN")

                        //to authenticate all the request
                        //so any request inside *authenticating http request*,  authenticate them before going forward inside controllers
                        .anyRequest().authenticated())

                //finally after disabling formLogin, now no Jsession id will be generated and we won't be able to access any protected routes. Only "/posts" is accessible
                //to access those we'll be using jwt
//                .formLogin(formLoginConfig -> formLoginConfig.permitAll())

                //to define it with default behaviour, like "/login" for login and default login form.
//                .formLogin(Customizer.withDefaults())

                //csrf token is passed inside our authentication request, every time we try to authenticate
                //to configure CSRF token

                //to disable csrf
                .csrf(csrfConfig -> csrfConfig.disable())
                //in lambda form
                //.csrf(AbstractHttpConfigurer::disable)

                //to go stateless
                .sessionManagement(sessionConfig -> sessionConfig
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        //to remove session-id to go to stateless in production ready

                //to define new route for login page
//                .formLogin(formLoginConfig -> formLoginConfig
                        //for this we have to define "newLogin.html" inside our /resources/templates
//                        .loginPage("/newLogin.html"))
                //this default formLogin behaviour in not much used instead jwt is used. jwt is new norm for handling security these days
        ;


        //return SecurityFilterChain from httpSecurity
        return httpSecurity.build();
    }



    //This would be mainly used for testing purpose. In production, we'll be using storing details inside database (here using "UserService")
    //inside application.properties we can create only one user
    //to create multiple users with different role for testing
    //we can do that using inMemory user details service
    @Bean
    UserDetailsService myInMemoryUserDetailsService() {
        //import "User" from spring security ".core.userDetails" and not from ".entities" because that is our user.
        UserDetails normalUser = User
                .withUsername("aryan")
                //do not directly add the password else it will return an error. It needs password encoder for it to work i.e. to encode password.
                //.password()
                .password(passwordEncoder().encode("Aryan123"))
                .roles("USER")
                .build();

        UserDetails adminUser = User
                .withUsername("admin")
                .password(passwordEncoder().encode("admin"))
                .roles("ADMIN")
                //can put any kind of string for the role
                //.roles("MANAGER")
                .build();

        //TO REGISTER USERS inside our inMemoryUserDetailService
        return new InMemoryUserDetailsManager(normalUser, adminUser);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
