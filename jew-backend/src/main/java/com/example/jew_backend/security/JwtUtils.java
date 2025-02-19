package com.example.jew_backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;

@Component
public class JwtUtils {

    private static final Logger LOGGER = Logger.getLogger(JwtUtils.class.getName());

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtUtils(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access.expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh.expiration}") long refreshTokenExpiration
    ) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    // 🔹 Tạo Access Token (Có email và role)
    public String generateAccessToken(String email, String role) {
        return createToken(email, role, accessTokenExpiration);
    }

    // 🔹 Tạo Refresh Token (Có email nhưng không cần role)
    public String generateRefreshToken(String email) {
        return createToken(email, null, refreshTokenExpiration);
    }

    // 🔹 Hàm chung để tạo token
    private String createToken(String email, String role, long expirationTime) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        // 🔹 Thêm thông tin email + role vào token
        JwtBuilder jwtBuilder = Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey, SignatureAlgorithm.HS256);

        // Thay thế setSubject bằng addClaims để lưu email dưới thuộc tính "email"
        jwtBuilder.addClaims(Map.of("email", email));

        if (role != null) {
            jwtBuilder.addClaims(Map.of("role", role)); // Thêm role vào payload
        }

        return jwtBuilder.compact();
    }


    // 🔹 Kiểm tra JWT hợp lệ không
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            LOGGER.warning("Token has expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            LOGGER.warning("Unsupported JWT token: " + e.getMessage());
        } catch (MalformedJwtException e) {
            LOGGER.warning("Malformed JWT token: " + e.getMessage());
        } catch (SecurityException e) {
            LOGGER.warning("Invalid JWT signature: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid JWT token: " + e.getMessage());
        }
        return false;
    }

    // 🔹 Lấy email từ JWT
    public String getEmailFromToken(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }

    // 🔹 Lấy role từ JWT
    public String getUserRoleFromToken(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    // 🔹 Hàm lấy thông tin bất kỳ từ JWT
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = parseToken(token);
        return claims != null ? claimsResolver.apply(claims) : null;
    }

    // 🔹 Giải mã JWT và lấy dữ liệu payload
    private Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            LOGGER.warning("Failed to parse JWT token: " + e.getMessage());
            return null;
        }
    }
}
