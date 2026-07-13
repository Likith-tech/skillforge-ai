package com.skillforge.support;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Populates the SecurityContext with a real {@code SecurityUser} (not Spring
 * Security's generic {@code User}), because every controller in this app casts
 * {@code authentication.getPrincipal()} to {@code SecurityUser} to read the
 * caller's numeric id. Plain {@code @WithMockUser} would make that cast throw
 * ClassCastException in every controller test, so we need this instead.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@WithSecurityContext(factory = WithMockSecurityUserFactory.class)
public @interface WithMockSecurityUser {

    long id() default 1L;

    String email() default "user@example.com";

    /** One of STUDENT, RECRUITER, ADMIN. */
    String role() default "STUDENT";
}
