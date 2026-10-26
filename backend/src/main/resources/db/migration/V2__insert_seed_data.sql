
INSERT INTO users (id, email, password_hash, auth_provider, role, first_name, last_name, created_at)
VALUES (
           'a4bab6f2-60fa-461a-b7b5-746b0e883896',
           'test.citizen@example.com',
           'stub',
           'LOCAL',
           'CITIZEN',
           'john',
           'doe',
           NOW()
       );

INSERT INTO threat_types (id, name, icon) VALUES
                                              ('11111111-1111-1111-1111-111111111111', 'fire', 'fire_icon'),
                                              ('22222222-2222-2222-2222-222222222222', 'flood', 'flood_icon'),
                                              ('33333333-3333-3333-3333-333333333333', 'traffic accident', 'accident_icon'),
                                              ('44444444-4444-4444-4444-444444444444', 'infrastructure failure', 'infrastructure_icon');