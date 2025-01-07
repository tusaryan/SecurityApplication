package com.tusaryan.SpringSecurityApp.SecurityApplication.services;

import com.tusaryan.SpringSecurityApp.SecurityApplication.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Set;

//Earlier, W6.1

//to use this service and check how it is passing the user and getting data back from token, we'll create test cases inside SecurityApplicationTests class.
@Service
public class JwtService {

    //to get the secret key from application.properties file
    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    //to create a key
    private SecretKey getSecretKey(){
        //inside Keys class there is a method name hmacShaKeyFor which takes our secret key as argument, and then we convert that String/key to bytes
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    //make sure to import User from entities package
    public String generateAccessToken(User user) {
        return Jwts.builder()
                //since id is in long so converting it to string
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                //hard coding the role for now that each user generated will have role of ADMIN and USER. Later we'll change this behaviour
                .claim("roles", Set.of("ADMIN", "USER"))
                //assigning issue date to be current time
                .issuedAt(new Date())
                //setting expiration time to be 1 min after issue time
                .expiration(new Date(System.currentTimeMillis() + 1000*60*10))
                //sign it with the secret key
                .signWith(getSecretKey())
                //return the token
                .compact();

    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                //since id is in long so converting it to string
                .subject(user.getId().toString())

                //since we are not using refresh token for access, so we removed email and roles from here

                //assigning issue date to be current time
                .issuedAt(new Date())
                //setting expiration time to be 1 min after issue time
                .expiration(new Date(System.currentTimeMillis() + 1000L *60*60*24*30*6))
                //sign it with the secret key
                .signWith(getSecretKey())
                //return the token
                .compact();

    }

    //to get something from the token like get user id from jwt token
    public Long getUserIdFromToken(String token){
        //to decode this token

        //now understand that this token can get expired/or maybe it is not valid, so in that case we could get some exceptions here. Later we'll handle this exception
        //get claims out of it
        Claims claims = Jwts.parser()
                //verify the token with the secret key we have
                .verifyWith(getSecretKey())
                //get the token, upto here we created a builder with the help of our Secret key
                .build()
                //parse this token. it will check if the token is valid or not and if valid only then it will return the payload
                .parseSignedClaims(token)
                //get/return the payload, this payload is actually the claims and all other info we passed during the generateToken() method
                .getPayload();

        //since we stored userId inside the subject during generateToken() method, so we can get it from here and return it.
        //since it is Long so we'll cast or wrap it to Long, use option+enter to do that
        //return claims.getSubject());
        return Long.valueOf(claims.getSubject());
    }
}
