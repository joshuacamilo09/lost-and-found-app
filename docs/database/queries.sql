-- Contar itens publicados pelo utilizador 1
SELECT COUNT(*) FROM items WHERE user_id = 1;

-- Contar itens que o utilizador 1 conseguiu resolver/devolver
SELECT COUNT(*) FROM items WHERE user_id = 1 AND resolved = TRUE;


-- Listagem de conversas
SELECT c.id, i.title, u.username AS contact_name, c.last_message_at
FROM conversations c
JOIN items i ON c.item_id = i.id
JOIN conversation_participants cp ON c.id = cp.conversation_id
JOIN users u ON cp.user_id = u.id
WHERE cp.user_id = 1 AND u.id != 1
ORDER BY c.last_message_at DESC;

-- Historico de mensagens de um chat
SELECT m.content, m.sent_at, u.username AS sender
FROM messages m
JOIN users u ON m.sender_id = u.id
WHERE m.conversation_id = 10
ORDER BY m.sent_at ASC;