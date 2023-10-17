package com.amigos.securitytestone.config;

import static com.amigos.securitytestone.model.Permission.*;
import static com.amigos.securitytestone.model.Role.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import lombok.RequiredArgsConstructor;;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

        @Autowired
        JwtAuthenticationFilter jwtAuthFilter;

        @Autowired
        AuthenticationProvider authenticationProvider;

        @Autowired
        LogoutHandler logoutHandler;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .authorizeHttpRequests(
                                                authorizeHttp -> {
                                                        authorizeHttp.requestMatchers("/api/v1/auth/**").permitAll();

                                                        authorizeHttp.requestMatchers("/api/v1/management/**")
                                                                        .hasAnyRole(ADMIN.name(),
                                                                                        MANAGER.name());
                                                        authorizeHttp.requestMatchers(HttpMethod.GET,
                                                                        "/api/v1/management/**")
                                                                        .hasAnyAuthority(ADMIN_READ.name(),
                                                                                        MANAGER_READ.name());
                                                        authorizeHttp.requestMatchers(HttpMethod.POST,
                                                                        "/api/v1/management/**")
                                                                        .hasAnyAuthority(ADMIN_CREATE.name(),
                                                                                        MANAGER_CREATE.name());
                                                        authorizeHttp.requestMatchers(HttpMethod.PUT,
                                                                        "/api/v1/management/**")
                                                                        .hasAnyAuthority(ADMIN_UPDATE.name(),
                                                                                        MANAGER_UPDATE.name());
                                                        authorizeHttp.requestMatchers(HttpMethod.DELETE,
                                                                        "/api/v1/management/**")
                                                                        .hasAnyAuthority(ADMIN_DELETE.name(),
                                                                                        MANAGER_DELETE.name());

                                                        authorizeHttp.anyRequest().authenticated();
                                                })
                                .sessionManagement(
                                                sessionManagement -> sessionManagement
                                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                                .logout(logout -> logout
                                                .logoutUrl("/api/v1/auth/logout")
                                                .addLogoutHandler(logoutHandler)
                                                .logoutSuccessHandler((req, res, auth) -> SecurityContextHolder
                                                                .clearContext()));
                return http.build();
        }
}
