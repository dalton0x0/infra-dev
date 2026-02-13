package com.cheridanh.infradev.configs;

import com.cheridanh.infradev.dtos.response.ErrorResponse;
import com.cheridanh.infradev.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authorizationHeader.substring(BEARER_PREFIX.length());

        try {
            String username = jwtUtil.extractUsername(jwt);
            log.debug("Token JWT détecté pour l'utilisateur : {}", username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                authenticateUser(request, jwt, username);
            }

        } catch (ExpiredJwtException ex) {
            log.warn("Token JWT expiré sur la requête : {} {}", request.getMethod(), request.getRequestURI());
            log.debug("ExpiredJwtException : {}", ex.getMessage());
            sendErrorResponse(response, request, "Token expiré", "Le token d'accès a expiré, veuillez le renouveler");
            return;

        } catch (JwtException ex) {
            log.warn("Token JWT invalide sur la requête : {} {} - Cause : {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
            sendErrorResponse(response, request, "Token invalide", "Le token d'accès est invalide");
            return;

        } catch (UsernameNotFoundException ex) {
            log.warn("Utilisateur du token JWT introuvable sur : {} {}", request.getMethod(), request.getRequestURI());
            log.debug("UsernameNotFoundException : {}", ex.getMessage());
            sendErrorResponse(response, request, "Utilisateur introuvable", "L'utilisateur associé au token n'existe plus");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateUser(HttpServletRequest request, String jwt, String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (jwtUtil.validateToken(jwt, userDetails)) {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("Authentification JWT réussie pour : {}", username);
        } else {
            log.warn("Validation du token JWT échouée pour : {}", username);
        }
    }

    private void sendErrorResponse(
            HttpServletResponse response,
            HttpServletRequest request,
            String error,
            String message) throws IOException {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(error)
                .message(message)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        OBJECT_MAPPER.writeValue(response.getOutputStream(), errorResponse);
    }
}
