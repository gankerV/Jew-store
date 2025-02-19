package com.example.jew_backend.config;

import com.example.jew_backend.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Tắt CSRF để tránh lỗi 403 với POST, PUT, DELETE
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Không sử dụng session
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/shop/**").permitAll() // Ai cũng truy cập được
                        .requestMatchers("/api/user/login", "/api/user/register", "/api/user/refresh-token").permitAll()// Login, Register, và refresh token không cần xác thực
                        .requestMatchers("/api/gold-prices").permitAll()
                        .requestMatchers("POST", "/api/gold-prices/**").hasRole("ADMIN")
                        .requestMatchers("PUT", "/api/gold-prices/**").hasRole("ADMIN")
                        .requestMatchers("DELETE", "/api/gold-prices/**").hasRole("ADMIN")// Chỉ admin mới có quyền cập nhật giá vàng
                        .anyRequest().authenticated() // Các API khác cần xác thực
                )
                .formLogin(form -> form.disable()) // Tắt form login mặc định của Spring Security
                .httpBasic(httpBasic -> httpBasic.disable()) // Tắt HTTP Basic Authentication
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // Thêm bộ lọc JWT
                .cors(cors -> cors.configurationSource(corsConfigurationSource())); // Cấu hình CORS tùy chỉnh

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Cung cấp PasswordEncoder sử dụng BCrypt
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // Cho phép gửi cookies
        config.addAllowedOrigin("http://localhost:5173"); // Cho phép origin từ frontend
        config.addAllowedOrigin("http://localhost:5174");
        config.addAllowedHeader("*"); // Cho phép tất cả headers
        config.addAllowedMethod("*"); // Cho phép tất cả các HTTP method (GET, POST, PUT, DELETE, ...)
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
