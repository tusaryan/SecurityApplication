package com.tusaryan.SpringSecurityApp.SecurityApplication.utils;

import com.tusaryan.SpringSecurityApp.SecurityApplication.entities.enums.Permission;
import com.tusaryan.SpringSecurityApp.SecurityApplication.entities.enums.Role;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tusaryan.SpringSecurityApp.SecurityApplication.entities.enums.Permission.*;
import static com.tusaryan.SpringSecurityApp.SecurityApplication.entities.enums.Role.*;

//W6.5

public class PermissionMapping {

    private static final Map<Role, Set<Permission>> map = Map.of(
            USER, Set.of(USER_VIEW, POST_VIEW),
            CREATOR, Set.of(POST_CREATE, USER_UPDATE, POST_UPDATE),
            //Since inside authorities we're creating set of authorities so if we have multiple duplicate authorities, then it will be automatically made unique
            ADMIN, Set.of(POST_CREATE, USER_UPDATE, POST_UPDATE, USER_DELETE, USER_CREATE, POST_DELETE)
    );

    //to return all the permissions related to a particular user roles
    public static Set<SimpleGrantedAuthority> getAuthoritiesForRole(Role role) {
        //converting the set of roles to a set of simple authorities
        return map.get(role).stream()
                .map(permission -> new SimpleGrantedAuthority(permission.name()))
                .collect(Collectors.toSet());
    }

}
