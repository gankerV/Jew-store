package com.example.jew_backend.controller;

import com.example.jew_backend.security.JwtUtils;
import com.example.jew_backend.model.User;
import com.example.jew_backend.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    // API đăng nhập
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody User user, HttpServletResponse response) {
        boolean isAuthenticated = userService.authenticate(user.getEmail(), user.getPassword());

        Map<String, String> result = new HashMap<>();
        if (isAuthenticated) {
            result.put("message", "Đăng nhập thành công!");

            // Lấy vai trò của user từ database
            String role = userService.getUserRole(user.getEmail());  // Lấy role từ database

            // Tạo Access Token & Refresh Token chứa role
            String accessToken = jwtUtils.generateAccessToken(user.getEmail(), role);
            String refreshToken = jwtUtils.generateRefreshToken(user.getEmail());

            // Lưu Refresh Token vào HttpOnly Cookie
            Cookie refreshCookie = new Cookie("REFRESH_TOKEN", refreshToken);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(false); // Đổi thành true nếu dùng HTTPS
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7 ngày

            response.addCookie(refreshCookie);

            // Trả về Access Token và Role trong response
            result.put("accessToken", accessToken);
            result.put("role", role);  // Trả về role cho frontend biết

            return ResponseEntity.ok(result);
        } else {
            result.put("message", "Email hoặc mật khẩu không đúng");
            return ResponseEntity.status(401).body(result);
        }
    }


    // API đăng xuất
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletResponse response) {
        System.out.println(1);
        // Xóa cookie Refresh Token
        Cookie cookie = new Cookie("REFRESH_TOKEN", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Xóa ngay lập tức

        response.addCookie(cookie);

        Map<String, String> result = new HashMap<>();
        result.put("message", "Đăng xuất thành công!");

        return ResponseEntity.ok(result);
    }

    // API xác thực Access Token
    @GetMapping("/validate-token")
    public ResponseEntity<Map<String, String>> validateToken(@RequestHeader(name = "Authorization", required = false) String token) {
        Map<String, String> response = new HashMap<>();

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Bỏ "Bearer "

            if (jwtUtils.validateToken(token)) {
                String email = jwtUtils.getEmailFromToken(token);
                response.put("message", "Token hợp lệ. Người dùng: " + email);
                return ResponseEntity.ok(response);
            }
        }

        response.put("message", "Token không hợp lệ hoặc đã hết hạn");
        return ResponseEntity.status(401).body(response);
    }

    // API cấp lại Access Token
    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshToken(
            @CookieValue(name = "REFRESH_TOKEN", required = false) String refreshToken,
            HttpServletResponse response) {

        Map<String, String> result = new HashMap<>();

        if (refreshToken != null && jwtUtils.validateToken(refreshToken)) {
            String email = jwtUtils.getEmailFromToken(refreshToken);
            String role = userService.getUserRole(email); // Lấy role từ email có được trong refresh token
            String newAccessToken = jwtUtils.generateAccessToken(email, role); // Cấp lại access token với email và role

            result.put("accessToken", newAccessToken);
            return ResponseEntity.ok(result);
        }

        result.put("message", "Refresh token không hợp lệ hoặc đã hết hạn");
        return ResponseEntity.status(401).body(result);
    }

    // API đăng ký user mới
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody User user) {
        boolean isRegistered = userService.registerUser(user);

        Map<String, String> response = new HashMap<>();
        if (isRegistered) {
            response.put("message", "Đăng ký thành công!");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Email đã tồn tại!");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
