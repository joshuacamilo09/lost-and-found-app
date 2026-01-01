-- Inserir Categorias base
INSERT INTO categories (name, description) VALUES 
('Eletrónicos', 'Telemóveis, portáteis, câmaras'),
('Documentos', 'Cartão de cidadão, passaporte, cartas'),
('Animais', 'Cães, gatos e outros animais de estimação'),
('Vestuário', 'Casacos, mochilas, acessórios');

-- Inserir Utilizador de Teste (Password: '123456' - Exemplo)
INSERT INTO users (username, email, password, full_name, location) VALUES 
('joaosilva', 'joao@email.com', '$2a$10$eImiUi..', 'João Silva', 'Porto');

-- Inserir um Item
INSERT INTO items (title, description, status, image_url, latitude, longitude, user_id, category_id, item_date_time) VALUES 
('iPhone 13 Azul', 'Perdido no metro da Trindade', 'LOST', 'https://res.cloudinary.com/demo/image.png', 41.15, -8.61, 1, 1, NOW());