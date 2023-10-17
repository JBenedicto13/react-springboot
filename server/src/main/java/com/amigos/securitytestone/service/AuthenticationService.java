package com.amigos.securitytestone.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.amigos.securitytestone.config.JwtService;
import com.amigos.securitytestone.controller.AuthenticationRequest;
import com.amigos.securitytestone.controller.AuthenticationResponse;
import com.amigos.securitytestone.controller.RegisterRequest;
import com.amigos.securitytestone.model.User;
import com.amigos.securitytestone.repository.MyRepository;
import com.amigos.securitytestone.token.Token;
import com.amigos.securitytestone.token.TokenRepository;
import com.amigos.securitytestone.token.TokenType;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.var;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

        @Autowired
        MyRepository repository;

        @Autowired
        TokenRepository tokenRepository;

        @Autowired
        PasswordEncoder passwordEncoder;

        @Autowired
        JwtService jwtService;

        private final AuthenticationManager authenticationManager;

        public AuthenticationResponse register(RegisterRequest request) {
                var user = User.builder()
                                .firstname(request.getFirstname())
                                .lastname(request.getLastname())
                                .email(request.getEmail())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .role(request.getRole())
                                .build();
                var savedUser = repository.save(user);
                var jwtToken = jwtService.generateToken(user);
                savedUserToken(savedUser, jwtToken);
                var refreshToken = jwtService.generateRefreshToken(user);
                return AuthenticationResponse.builder()
                                .accessToken(jwtToken)
                                .refreshToken(refreshToken)
                                .build();
        }

        private void savedUserToken(User user, String jwtToken) {
                var token = Token.builder()
                                .user(user)
                                .token(jwtToken)
                                .tokenType(TokenType.BEARER)
                                .revoked(false)
                                .expired(false)
                                .build();
                tokenRepository.save(token);
        }

        private void revokeAllUserTokens(User user) {
                var validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());
                if (validUserTokens.isEmpty())
                        return;
                validUserTokens.forEach(t -> {
                        t.setExpired(true);
                        t.setRevoked(true);
                });
                tokenRepository.saveAll(validUserTokens);
        }

        public AuthenticationResponse authenticate(AuthenticationRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getPassword()));

                var user = repository.findByEmail(request.getEmail())
                                .orElseThrow();
                var jwtToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                savedUserToken(user, jwtToken);
                var refreshToken = jwtService.generateRefreshToken(user);
                return AuthenticationResponse.builder()
                                .accessToken(jwtToken)
                                .refreshToken(refreshToken)
                                .build();
        }

        public void refreshToken(
                        HttpServletRequest request,
                        HttpServletResponse response) throws IOException {
                final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
                final String refreshToken;
                final String userEmail;

                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        return;
                }

                refreshToken = authHeader.substring(7);
                // userEmail todo extract the userEmail from JWT Token
                userEmail = jwtService.extractUsername(refreshToken);

                if (userEmail != null) {
                        var user = this.repository.findByEmail(userEmail).orElseThrow();
                        if (jwtService.isTokenValid(refreshToken, user)) {
                                var accessToken = jwtService.generateToken(user);
                                revokeAllUserTokens(user);
                                savedUserToken(user, accessToken);
                                var authResponse = AuthenticationResponse.builder()
                                                .accessToken(accessToken)
                                                .refreshToken(refreshToken)
                                                .build();
                                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);

                        }
                }
        }

}
