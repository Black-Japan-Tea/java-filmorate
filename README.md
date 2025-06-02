# java-filmorate
![]()

# Структура базы данных

Данная структура позволяет:
- Хранить дружеские связи между пользователями
- Учитывать лайки фильмов
- Классифицировать фильмы по жанрам и возрастным рейтингам без дублирования данных

## Примеры SQL-запросов

### Получение информации о пользователе
```sql
SELECT id, name, birthday, login, email 
FROM users 
WHERE id = 1;
```

### Получение списка друзей пользователя
```sql
SELECT u.id, u.name, u.login
FROM users u
JOIN friendships f ON u.id = f.friend_id
WHERE f.user_id = 1 AND f.status = 'CONFIRMED';
```

### Поиск фильмов по названию
```sql
SELECT f.id, f.name, f.description, f.release_date, f.duration, m.name AS mpa_rating
FROM films f
JOIN mpa_ratings m ON f.mpa_rating_id = m.id
WHERE f.name LIKE '%Интерстеллар%';
```

### Получение фильмов с определенным рейтингом MPA
```sql
SELECT f.id, f.name, f.release_date
FROM films f
JOIN mpa_ratings m ON f.mpa_rating_id = m.id
WHERE m.name = 'PG-13';
```

### Получение самых популярных фильмов по количеству лайков
```sql
SELECT f.id, f.name, COUNT(l.user_id) AS likes_count
FROM films f
LEFT JOIN likes l ON f.id = l.film_id
GROUP BY f.id, f.name
ORDER BY likes_count DESC
LIMIT 10;
```

### Получение рекомендаций (фильмы, которые лайкнули друзья)
```sql
SELECT DISTINCT f.id, f.name, f.description
FROM films f
JOIN likes l ON f.id = l.film_id
JOIN friendships fr ON l.user_id = fr.friend_id
WHERE fr.user_id = 1
AND fr.status = 'CONFIRMED'
AND f.id NOT IN (SELECT film_id FROM likes WHERE user_id = 1);
```
