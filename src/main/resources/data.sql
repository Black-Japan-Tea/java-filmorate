INSERT INTO mpa_rate (name)
SELECT 'G'
    WHERE NOT EXISTS (SELECT 1
                  FROM mpa_rate
                  WHERE name = 'G');

INSERT INTO mpa_rate (name)
SELECT 'PG'
    WHERE NOT EXISTS (SELECT 1
                  FROM mpa_rate
                  WHERE name = 'PG');

INSERT INTO mpa_rate (name)
SELECT 'PG-13'
    WHERE NOT EXISTS (SELECT 1
                  FROM mpa_rate
                  WHERE name = 'PG-13');

INSERT INTO mpa_rate (name)
SELECT 'R'
    WHERE NOT EXISTS (SELECT 1
                  FROM mpa_rate
                  WHERE name = 'R');

INSERT INTO mpa_rate (name)
SELECT 'NC-17'
    WHERE NOT EXISTS (SELECT 1
                  FROM mpa_rate
                  WHERE name = 'NC-17');

INSERT INTO genres (name)
SELECT 'Комедия'
    WHERE NOT EXISTS (SELECT 1
                  FROM genres
                  WHERE name = 'Комедия');

INSERT INTO genres (name)
SELECT 'Драма'
    WHERE NOT EXISTS (SELECT 1
                  FROM genres
                  WHERE name = 'Драма');

INSERT INTO genres (name)
SELECT 'Мультфильм'
    WHERE NOT EXISTS (SELECT 1
                  FROM genres
                  WHERE name = 'Мультфильм');

INSERT INTO genres (name)
SELECT 'Триллер'
    WHERE NOT EXISTS (SELECT 1
                  FROM genres
                  WHERE name = 'Триллер');

INSERT INTO genres (name)
SELECT 'Документальный'
    WHERE NOT EXISTS (SELECT 1
                  FROM genres
                  WHERE name = 'Документальный');


INSERT INTO genres (name)
SELECT 'Боевик'
    WHERE NOT EXISTS (SELECT 1
                  FROM genres
                  WHERE name = 'Боевик');