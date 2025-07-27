-- ---------- SCHEMA DEFINITIE ----------

DROP TABLE IF EXISTS order_products;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS users_roles;
DROP TABLE IF EXISTS user_profile;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS product;

-- ---------- ROLES TABEL ----------
CREATE TABLE roles (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(50) NOT NULL UNIQUE
);

-- ---------- USERS TABEL ----------
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       enabled BOOLEAN NOT NULL,
                       name VARCHAR(100),
                       address VARCHAR(255)
);

-- ---------- USER_PROFILE TABEL ----------
CREATE TABLE user_profile (
                              id SERIAL PRIMARY KEY,
                              phone VARCHAR(20),
                              birth_date DATE,
                              user_id INT UNIQUE,
                              FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ---------- USERS_ROLES TABEL ----------
CREATE TABLE users_roles (
                             user_id INT NOT NULL,
                             role_id INT NOT NULL,
                             PRIMARY KEY (user_id, role_id),
                             FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                             FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- ---------- PRODUCT TABEL ----------
CREATE TABLE product (
                         id SERIAL PRIMARY KEY,
                         title VARCHAR(255) NOT NULL,
                         author VARCHAR(255),
                         description TEXT,
                         price DECIMAL(10, 2),
                         rental_price DECIMAL(10, 2),
                         image_url VARCHAR(255),
                         file_path VARCHAR(255),
                         file_type VARCHAR(50)
);

-- ---------- ORDERS TABEL ----------
CREATE TABLE orders (
                        id SERIAL PRIMARY KEY,
                        order_date TIMESTAMP NOT NULL,
                        type VARCHAR(20) NOT NULL, -- 'koop' of 'huur'
                        total_amount DECIMAL(10, 2) NOT NULL,
                        user_id INT,
                        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- ---------- ORDER_PRODUCTS KOPPELTABEL ----------
CREATE TABLE order_products (
                                order_id INT NOT NULL,
                                product_id INT NOT NULL,
                                PRIMARY KEY (order_id, product_id),
                                FOREIGN KEY (order_id) REFERENCES_
