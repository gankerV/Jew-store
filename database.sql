-- Xóa database nếu đã tồn tại
DROP DATABASE IF EXISTS shopjew;

-- Tạo lại database
CREATE DATABASE shopjew;
-- Sử dụng database mới tạo
USE shopjew;

-- Tạo bảng products
CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL
);

-- Thêm dữ liệu mẫu vào bảng products
INSERT INTO products (name, price) VALUES 
('Dây chuyền đá', 10500.00),
('Nhẫn kim tiềnusers', 2200.00),
('Bông tai cưới', 1250.00),
('Lắc chân', 800.00),
('Mặt phật', 1100.00);

-- Tạo bảng users
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('CUSTOMER', 'STAFF', 'ADMIN') NOT NULL DEFAULT 'CUSTOMER' -- Chỉnh ENUM thành chữ HOA
);


CREATE TABLE gold_price (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    priceBuy DECIMAL(10,2) NOT NULL,
    priceSold DECIMAL(10,2) NOT NULL
);

INSERT INTO gold_price (name, priceBuy, priceSold) VALUES 
('9t8', 8000, 8850),
('9t9', 8050, 8900),
('9999', 8880, 9050),
('SJC', 9000, 9300);

