package com.example.jew_backend.service;

import com.example.jew_backend.model.User;
import com.example.jew_backend.model.User.Role;
import com.example.jew_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Inject thay vì khởi tạo thủ công

    // Lấy danh sách tất cả user
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Tìm user theo ID
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // Lấy role của user theo email và trả về dưới dạng String (USER, ADMIN)
    public String getUserRole(String email) {
        return userRepository.findByEmail(email)
                .map(user -> user.getRole().name()) // Trả về tên role dưới dạng String
                .orElse(null); // Trả về null nếu không tìm thấy user
    }

    // Đăng ký user mới
    public boolean registerUser(User user) {
        if (user.getRole() == null) {
            user.setRole(Role.CUSTOMER); // Mặc định nếu không có role
        }
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Mã hóa mật khẩu
        return userRepository.save(user) != null;
    }

    // Xác thực đăng nhập
    public boolean authenticate(String email, String password) {
        return userRepository.findByEmail(email)
                .map(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElse(false);
    }
}
