-- ---------- DATA ----------

-- Rollen (met ROLE_ prefix, verplicht voor Spring Security)
INSERT INTO roles (name) VALUES ('ROLE_USER');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');

-- Gebruikers
-- wachtwoord = Admin123!
INSERT INTO users (email, password, enabled, name, address)
VALUES (
           'admin@webbazar.com',
           '$2a$10$BpLp3KI657cxKHvZXB0ne.a2AVAs4BfmPTVY3y.jIo.Y31xiNIupu',
           true,
           'Admin Naam',
           'Straat 2'
       );

-- wachtwoord = User123!
INSERT INTO users (email, password, enabled, name, address)
VALUES (
           'user@webbazar.com',
           '$2a$10$f4UMvmye/pWVa02wI7Ac/.Vbu94BM4s/pPtnSra3eu5sCfIHgrXCm',
           true,
           'User Naam',
           'Straat 1'
       );

-- wachtwoord = Test123!
INSERT INTO users (email, password, enabled, name, address)
VALUES (
           'test@webbazar.com',
           '$2a$10$7asKqPIwK3elM8r7Uq14dePoeIwdGaTBHV7V2AWRjYzFx2ZJVbUt.',
           true,
           'Test Gebruiker',
           'Testlaan 3'
       );

-- Rollen koppelen
INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'admin@webbazar.com' AND r.name = 'ROLE_ADMIN';

INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'user@webbazar.com' AND r.name = 'ROLE_USER';

INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'test@webbazar.com' AND r.name = 'ROLE_USER';

-- 📚 Voorbeeldboeken toevoegen
INSERT INTO product (title, price, rental_price, image_url)
VALUES
    ('De Alchemist', 9.99, 2.99, 'https://via.placeholder.com/150?text=De+Alchemist'),
    ('Sapiens', 12.49, 3.49, 'https://via.placeholder.com/150?text=Sapiens'),
    ('Atomic Habits', 10.99, 3.19, 'https://via.placeholder.com/150?text=Atomic+Habits');
