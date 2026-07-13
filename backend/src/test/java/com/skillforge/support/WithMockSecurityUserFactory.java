package com.skillforge.support;

import com.skillforge.model.Role;
import com.skillforge.model.RoleName;
import com.skillforge.model.User;
import com.skillforge.security.SecurityUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Set;

public class WithMockSecurityUserFactory implements WithSecurityContextFactory<WithMockSecurityUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockSecurityUser annotation) {
        User user = User.builder()
                .id(annotation.id())
                .fullName("Test User")
                .email(annotation.email())
                .password("irrelevant")
                .enabled(true)
                .roles(Set.of(new Role(RoleName.valueOf(annotation.role()))))
                .build();

        SecurityUser principal = new SecurityUser(user);
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authToken);
        return context;
    }
}
