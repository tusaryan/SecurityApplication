package com.tusaryan.SpringSecurityApp.SecurityApplication.config;

import com.tusaryan.SpringSecurityApp.SecurityApplication.entities.enums.Permission;
import com.tusaryan.SpringSecurityApp.SecurityApplication.entities.enums.Role;
import com.tusaryan.SpringSecurityApp.SecurityApplication.filters.JwtAuthFilter;
import com.tusaryan.SpringSecurityApp.SecurityApplication.handlers.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.tusaryan.SpringSecurityApp.SecurityApplication.entities.enums.Permission.*;
import static com.tusaryan.SpringSecurityApp.SecurityApplication.entities.enums.Role.ADMIN;
import static com.tusaryan.SpringSecurityApp.SecurityApplication.entities.enums.Role.CREATOR;

//Earlier, W5.6, 5.7, 6.2, 6.4, 6.5, 6.6

//Configuring SecurityFilterChain to customise default behaviour
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    private static final String[] publicRoutes = {
            "/error", "/auth/**", "/home.html"
    };

    //if we define web security like this than we have to define formLogin else it will not work.
    //since all the request have to go through the filter chain. So any incoming request will also have to pass through security filter chain

    //Removed this bean on L5.6 10:34 as not required now, useful for testing purpose only
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        //to configure filter chain
        httpSecurity

                //Each of the request have to be authenticated before they move further in the filter chain
                //to ask for authorization and pass auth i.e. login id and pass, inside
                .authorizeHttpRequests(auth -> auth

                        //To whitelist some routes i.e. to make those routes public and anyone can go to those routes.
                        //Inside () pass the pattern of request. For eg: pass in http methods like all get request are public or pass the type of url.
                        .requestMatchers(publicRoutes).permitAll()

                        //static import of ADMIN from Role enum
                        //to authorize your role(here specifically admin) if you want to access /posts and any route inside it like find posts by id.
//                        .requestMatchers("/posts/**").hasRole(ADMIN.name())

                        //we can also handle the GET, POST, PUT, DELETE and different types of http methods based upon user roles as well.
                        /*.requestMatchers(HttpMethod.GET, "/posts/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/posts/**")
                            .hasAnyRole(ADMIN.name(), CREATOR.name())

                        //so instead passing the roles, we can also pass the permissions/authorities now
                        .requestMatchers(HttpMethod.POST,"/posts/**").hasAnyAuthority(POST_CREATE.name())
//                        .requestMatchers(HttpMethod.GET,"/posts/**").hasAuthority(POST_VIEW.name())
                        .requestMatchers(HttpMethod.PUT,"/posts/**").hasAuthority(POST_UPDATE.name())
                        .requestMatchers(HttpMethod.DELETE,"/posts/**").hasAuthority(POST_DELETE.name())*/

                        //replacement of above
                        //to avoid cluttering of requestMatchers in the code, use requestMatchers to make any endpoint secure and authenticated
                        //and use Security methods inside api service/controller layers
                        .requestMatchers("/posts/**").authenticated()

                        //to authorize your role(here specifically admin) if you want to access /posts and any route inside it like find posts by id.
                        //Here only admins are allowed to go to these routes /posts/id
//                        .requestMatchers("/posts/**").hasAnyRole("ADMIN")
//                        .requestMatchers("/posts/**").authenticated()

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

                //To disable csrf
                .csrf(csrfConfig -> csrfConfig.disable())
                //in lambda form
                //.csrf(AbstractHttpConfigurer::disable)

                //To go stateless
                .sessionManagement(sessionConfig -> sessionConfig
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //this is how we add our custom filter inside Spring Security's default filter chain
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                //to add oauth2 login support
                .oauth2Login(oauth2Config -> oauth2Config
                        .failureUrl("/login?error=true")
                        .successHandler(oAuth2SuccessHandler)
                );
        //To remove session-id to go to stateless in production ready

                //To define new route for login page
//                .formLogin(formLoginConfig -> formLoginConfig
                        //for this we have to define "newLogin.html" inside our /resources/templates
//                        .loginPage("/newLogin.html"))
                //this default formLogin behaviour in not much used instead jwt is used. Jwt is new norm for handling security these days
        ;


        //Return SecurityFilterChain from httpSecurity
        return httpSecurity.build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }



    //This would be mainly used for testing purpose. In production, we'll be using storing details inside database (here using "UserService")
    //inside application.properties we can create only one user
    //to create multiple users with different role for testing
    //we can do that using inMemory user details service
    /*@Bean
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
    }*/



}
