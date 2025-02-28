package com.tusaryan.SpringSecurityApp.SecurityApplication.filters;

import com.tusaryan.SpringSecurityApp.SecurityApplication.entities.User;
import com.tusaryan.SpringSecurityApp.SecurityApplication.services.JwtService;
import com.tusaryan.SpringSecurityApp.SecurityApplication.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

//W5.7, 5.8, 6.4

@Component
@RequiredArgsConstructor
//we can name(class name/filter name) it anything we want
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    //@Autowired since we cannot use the constructor injection as this bean was created twice so it throws error.
    @Autowired
    //currently we are working on security filter chain context,
    //with the help of handlerException we can send the exception as response to the dispatcher servlet
    @Qualifier("handlerExceptionResolver")
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            //header are key-value pairs -> like here we interested in Authorization header, so key is "Authorization"
            final String requestTokenHeader = request.getHeader("Authorization");
            //token is in the form of "Bearer token", it is the nomenclature eg: "Bearer asfasdfasdfasdf", this means the client who is passing the token is a bearer of the token

            //if we don't have the token, then we don't need to do anything
            if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            //if we have the token, then we need to extract the token from the header
            String token = requestTokenHeader.split("Bearer ")[1]; /** ["", "asfasdfasdfasdf"] //spilt the token then get the 1st index string i.e. second part i.e. the token*/

            Long userId = jwtService.getUserIdFromToken(token);

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userService.getUserById(userId);
                UsernamePasswordAuthenticationToken authenticationToken =
                        //This is JwtAuth filter, here we are authenticating user, putting its credentials and passing the user authorities, inside the Security Context holder.
                        //Earlier just for demonstration purpose, we were passing null as authorities, but now we are passing the user.getAuthorities() to get the Role/Authority of user
//                        new UsernamePasswordAuthenticationToken(user, null, null);
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
            filterChain.doFilter(request, response);
        }
        //if we are not able to get the token correctly(from String token),
        // then we need to send the exception to the dispatcher servlet
        catch (Exception ex) {
            //(request, response, null, ex) -> null is the handler, ex is the exception
            handlerExceptionResolver.resolveException(request, response, null, ex);
        }
    }
}
