-- ========= ROLES =========
MERGE INTO roles (id, name) KEY(id) VALUES (1, 'ROLE_ADMIN');
MERGE INTO roles (id, name) KEY(id) VALUES (2, 'ROLE_USER');

-- ========= USERS =========
-- Wachtwoorden:
--   admin@webbazar.com -> Admin123!
--   user@webbazar.com  -> User123!
--   test@webbazar.com  -> Test123!

MERGE INTO users (email, password, name, address, enabled) KEY(email) VALUES
 ('admin@webbazar.com', '$2a$10$BpLp3KI657cxKHvZXB0ne.a2AVAs4BfmPTVY3y.jIo.Y31xiNIupu', 'Admin Naam', 'Straat 2', TRUE);

MERGE INTO users (email, password, name, address, enabled) KEY(email) VALUES
('user@webbazar.com',  '$2a$10$f4UMvmye/pWVa02wI7Ac/.Vbu94BM4s/pPtnSra3eu5sCfIHgrXCm', 'User Naam', 'Straat 1', TRUE);

MERGE INTO users (email, password, name, address, enabled) KEY(email) VALUES
('test@webbazar.com',  '$2a$10$7asKqPIwK3elM8r7Uq14dePoeIwdGaTBHV7V2AWRjYzFx2ZJVbUt.', 'Test Gebruiker', 'Testlaan 3', TRUE);

-- ========= USERS_ROLES (JOIN-TABEL) =========
MERGE INTO users_roles (user_id, role_id) KEY(user_id, role_id) VALUES (1, 1); -- admin -> ROLE_ADMIN
MERGE INTO users_roles (user_id, role_id) KEY(user_id, role_id) VALUES (1, 2); -- admin ook ROLE_USER
MERGE INTO users_roles (user_id, role_id) KEY(user_id, role_id) VALUES (2, 2); -- user  -> ROLE_USER
MERGE INTO users_roles (user_id, role_id) KEY(user_id, role_id) VALUES (3, 2); -- test  -> ROLE_USER

-- ========= PRODUCT SEED =========
MERGE INTO product (title, author, description, price, rental_price, file_path) KEY(title) VALUES
 ('Clean Code', 'Robert C. Martin', 'Klassieker over CLEAN code principes', 39.99, 3.99, 'uploads/books/clean-code Book.pdf');

MERGE INTO product (title, author, description, price, rental_price, file_path) KEY(title) VALUES
('Effective Java', 'Joshua Bloch', 'Best practices voor Java ontwikkelaars', 44.99, 4.49, 'uploads/books/Effective Java.pdf');

MERGE INTO product (title, author, description, price, rental_price, file_path) KEY(title) VALUES
('Design Patterns', 'Gamma et al.', 'Gang of Four patronen', 49.99, 4.99, 'uploads/books/Design Patterns.pdf');
