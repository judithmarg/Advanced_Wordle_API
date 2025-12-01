
INSERT INTO roles (name, created_at) 
VALUES ('ROLE_PLAYER', CURRENT_TIMESTAMP) 
ON CONFLICT (name) DO NOTHING;

INSERT INTO roles (name, created_at) 
VALUES ('ROLE_ADMIN', CURRENT_TIMESTAMP) 
ON CONFLICT (name) DO NOTHING;


-- El hash de abajo equivale a "Admin123" procesado por BCrypt
INSERT INTO users (username, email, password, active, created_at)
VALUES (
    'admin_master', 
    'admin@wordle.com', 
    '$2a$10$38dFPwvRtLdxirr5hurj.uSa6WaUNB8ERTnNI6rO78BOQtq0PBvFe', 
    true,
    CURRENT_TIMESTAMP
) ON CONFLICT (username) DO NOTHING;

INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'admin_master' AND r.name = 'ROLE_ADMIN'
ON CONFLICT DO NOTHING;