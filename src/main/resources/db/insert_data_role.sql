INSERT INTO role (id, role_name, description, created_at, updated_at)
VALUES
    (UUID(), 'ADMIN', 'Administrator role', NOW(), NOW()),
    (UUID(), 'USER', 'Standard user role', NOW(), NOW());
