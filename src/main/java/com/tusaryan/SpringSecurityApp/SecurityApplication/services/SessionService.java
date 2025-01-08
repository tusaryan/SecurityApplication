package com.tusaryan.SpringSecurityApp.SecurityApplication.services;


import com.tusaryan.SpringSecurityApp.SecurityApplication.entities.Session;
import com.tusaryan.SpringSecurityApp.SecurityApplication.entities.User;
import com.tusaryan.SpringSecurityApp.SecurityApplication.repositories.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

//L6.3

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;

    //we are hard coding the session limit to 2,
    // but we can also make it dynamic if we are making any subscription-based service, session they can have different session limit a/c to their subscription.
    private final int SESSION_LIMIT = 2;

    //create a new session
    public void generateNewSession(User user, String refreshToken) {

        //get all sessions of this user
        List<Session> userSession = sessionRepository.findByUser(user);

        //to check if the user has already reached/exceeded the session limit
        //also check with >=
        if (userSession.size() == SESSION_LIMIT) {
            //a/c to lastUsedAt time we sort the sessions
            userSession.sort(Comparator.comparing(Session::getLastUsedAt));
            //get the first user session
            Session leastRecentlyUsedSession = userSession.getFirst();
            //delete this user session or delete the least recently used session
            sessionRepository.delete(leastRecentlyUsedSession);
        }

        //if we reached here that means we have lesser user then the session limit.
        //create/build a new session
        Session newSession = Session.builder()
                .user(user)
                .refreshToken(refreshToken)
                .build();
        sessionRepository.save(newSession);
        //all other field like lastUsedAt and id will be automatically filled by hibernate
    }

    //to verify if the session is valid or not
    public void validateSession(String refreshToken) {
        //to check if there is a session present with this refresh token in our db or not
        Session session = sessionRepository.findByRefreshToken(refreshToken)
                //since this token may be expired, that's why we are passing the Refresh Token else we should never pass the refresh token.
                .orElseThrow(() -> new SessionAuthenticationException("Session not found for refreshToken: " + refreshToken));

        //every time we validate the session means are trying to access the session so we update the lastUsedAt time
        session.setLastUsedAt(LocalDateTime.now());
        //save this session
        sessionRepository.save(session);
    }

}
