-- Catálogo modelo de itens (Menu Items) para o order-service
-- Lista fixa de itens disponíveis para compor um pedido.

-- Tabela criada pelo Hibernate (ddl-auto=update). Inserções idempotentes:
-- usamos INSERT IGNORE para não duplicar caso o script rode mais de uma vez.

-- HAMBURGUER
INSERT IGNORE INTO menu_items (id, name, description, price, category, active) VALUES
    ('a0000001-0000-0000-0000-000000000001', 'X-Burger Classic',  'Beef burger with cheese, lettuce and tomato', 25.00, 'BURGER', 1),
    ('a0000001-0000-0000-0000-000000000002', 'X-Bacon',            'Beef burger with cheese and bacon',           30.00, 'BURGER', 1),
    ('a0000001-0000-0000-0000-000000000003', 'X-Tudo',             'Loaded burger with everything',                35.00, 'BURGER', 1);

-- PIZZA
INSERT IGNORE INTO menu_items (id, name, description, price, category, active) VALUES
    ('a0000002-0000-0000-0000-000000000001', 'Pizza Margherita',       'Medium - tomato, mozzarella and basil',  45.00, 'PIZZA', 1),
    ('a0000002-0000-0000-0000-000000000002', 'Pizza Calabresa',        'Medium - calabrian sausage and onion',    48.00, 'PIZZA', 1),
    ('a0000002-0000-0000-0000-000000000003', 'Pizza Quatro Queijos',   'Medium - four cheese blend',              52.00, 'PIZZA', 1);

-- BEBIDA
INSERT IGNORE INTO menu_items (id, name, description, price, category, active) VALUES
    ('a0000003-0000-0000-0000-000000000001', 'Coca-Cola 350ml',                'Soft drink',                7.00,  'DRINK', 1),
    ('a0000003-0000-0000-0000-000000000002', 'Natural Orange Juice 300ml',     'Fresh squeezed',            12.00, 'DRINK', 1),
    ('a0000003-0000-0000-0000-000000000003', 'Mineral Water 500ml',            'Still water',                5.00,  'DRINK', 1);

-- ACOMPANHAMENTO
INSERT IGNORE INTO menu_items (id, name, description, price, category, active) VALUES
    ('a0000004-0000-0000-0000-000000000001', 'French Fries Portion',           'Crispy fries',              18.00, 'SIDE', 1),
    ('a0000004-0000-0000-0000-000000000002', 'Caesar Salad',                   'Lettuce, parmesan, croutons', 22.00, 'SIDE', 1);

-- SOBREMESA
INSERT IGNORE INTO menu_items (id, name, description, price, category, active) VALUES
    ('a0000005-0000-0000-0000-000000000001', 'Petit Gateau',                   'Warm chocolate cake with ice cream', 20.00, 'DESSERT', 1);
