package com.example.sistema_citas_medicas_backend.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        logger.info("JwtRequestFilter - Request URI: " + path);

        // Permitir acceso público sin token a rutas específicas
        if (path.startsWith("/api/dashboard")) {
            logger.info("JwtRequestFilter - Acceso público permitido sin token");
            filterChain.doFilter(request, response);
            return;
        }

        final String authorizationHeader = request.getHeader("Authorization");
        logger.info("JwtRequestFilter - Authorization Header: " + authorizationHeader);

        String username = null; // En realidad será el ID en string
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                // Extraemos el "username" que es el ID del usuario en formato String
                username = jwtUtils.extractUsername(jwt);
                logger.info("JwtRequestFilter - Username extraído: " + username);
            } catch (Exception e) {
                logger.warn("JwtRequestFilter - Token inválido o expirado");
            }
        } else {
            logger.warn("JwtRequestFilter - Authorization header no presente o no comienza con Bearer");
        }

        // Si tenemos un ID extraído y no hay autenticación previa, validamos el token
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Cargamos los detalles del usuario usando el ID (string)
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Validamos el token con los detalles cargados
            if (jwtUtils.validateToken(jwt, userDetails)) {
                // Creamos la autenticación y la establecemos en el contexto de seguridad
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.info("JwtRequestFilter - Autenticación establecida para el usuario: " + username);
            } else {
                logger.warn("JwtRequestFilter - Token no válido para el usuario: " + username);
            }
        } else {
            logger.info("JwtRequestFilter - Username es null o autenticación ya establecida");
        }

        filterChain.doFilter(request, response);
    }

}
