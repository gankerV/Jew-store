package com.example.jew_backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = Logger.getLogger(JwtAuthenticationFilter.class.getName());

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String token = getTokenFromRequest(request);

        if (token != null && jwtUtils.validateToken(token)) {
            try {
                String email = jwtUtils.getEmailFromToken(token);
                String role = jwtUtils.getUserRoleFromToken(token);
                List<GrantedAuthority> authorities = getAuthoritiesFromRole(role);

                // 🔹 Tạo đối tượng Authentication cho SecurityContext
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(email, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                LOGGER.warning("JWT Authentication failed: " + e.getMessage());
            }
        }

        chain.doFilter(request, response);
    }

    // 🔹 Lấy JWT từ request
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Cắt bỏ "Bearer "
        }
        return null;
    }

    // 🔹 Lấy danh sách quyền từ role (chuyển thành GrantedAuthority)
    private List<GrantedAuthority> getAuthoritiesFromRole(String role) {
        return role != null ? List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())) : Collections.emptyList();
    }
}
