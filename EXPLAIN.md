# Анализ производительности запросов

## Пример поискового запроса

API endpoint: `GET /api/documents/search?status=DRAFT&from=2026-02-15&to=2026-02-22`

Соответствующий SQL (генерируется Hibernate):
```sql
SELECT * FROM documents 
WHERE status = 'DRAFT' 
  AND created_at BETWEEN '2026-02-15' AND '2026-02-22'
ORDER BY created_at DESC;
```
## Текущие индексы в проекте
Из Liquibase миграций созданы индексы только для внешних ключей:
- idx_history_document_id (на document_id в таблице history)
- idx_registry_document_id (на document_id в таблице registry)

Индексы на status и created_at в таблице documents отсутствуют.

### До добавления индексов
```
Sort (cost=9427.98..9580.08 rows=60839 width=75) (actual time=29.983..35.912 rows=60860 loops=1)
Sort Key: created_at DESC
Sort Method: external merge Disk: 5368kB
Buffers: shared hit=817, temp read=671 written=672
-> Seq Scan on documents (cost=0.00..1888.00 rows=60839 width=75) (actual time=0.069..6.463 rows=60860 loops=1)
Filter: ((created_at >= '2026-02-15 00:00:00'::timestamp without time zone) AND (created_at <= '2026-02-22 00:00:00'::timestamp without time zone) AND ((status)::text = 'DRAFT'::text))
Rows Removed by Filter: 340
Buffers: shared hit=817
Planning Time: 0.263 ms
Execution Time: 38.542 ms
```
### Что показывает этот результат:

- **61,000 документов** обработано (60,860 подходят по условиям, 340 отфильтровано)
- **Seq Scan** — полное сканирование таблицы (индексы на `status` и `created_at` отсутствуют)
- **Время выполнения:** 38.5 мс
- **Sort Method: external merge** — данные не поместились в память, использовался диск (медленнее)

## Добавленные индексы

В таблицу `documents` добавлены индексы для оптимизации поиска:

| Индекс | Поле | Назначение |
|--------|------|------------|
| `idx_documents_status` | `status` | Поиск по статусу документа |
| `idx_documents_author` | `author` | Поиск по автору |
| `idx_documents_created_at` | `created_at` | Поиск и сортировка по дате создания |

Эти индексы покрывают все параметры поискового запроса из API endpoint `GET /api/documents/search`.

### После добавления индексов

```
Index Scan Backward using idx_documents_created_at on documents  (cost=0.29..2878.29 rows=60851 width=75) (actual time=0.030..10.950 rows=60860 loops=1)
Execution Time: 12.857 ms
```

Результат: ускорение в 3 раза (38.5 мс → 12.9 мс)

## Заключение

Добавленные индексы на поля status, author и created_at значительно ускорят поисковые запросы и обеспечат хорошую 
производительность даже при росте количества документов в базе данных.

