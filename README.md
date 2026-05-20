# Notifications Service

Microservicio de notificaciones del sistema e-commerce. Registra y gestiona notificaciones para los usuarios sobre eventos relevantes como pagos, órdenes y reembolsos.

## Información general

| Campo | Valor |
|-------|-------|
| Puerto | `8089` |
| Base de datos | `db_notifications` (PostgreSQL) |
| Contexto | `/api/notifications` |

## Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| `POST` | `/api/notifications` | Crear notificación |
| `GET` | `/api/notifications/user/{userId}` | Todas las notificaciones de un usuario |
| `GET` | `/api/notifications/user/{userId}/unread` | Solo notificaciones no leídas |
| `GET` | `/api/notifications/user/{userId}/unread/count` | Cantidad de no leídas |
| `PATCH` | `/api/notifications/{id}/read` | Marcar notificación como leída |
| `PATCH` | `/api/notifications/user/{userId}/read-all` | Marcar todas como leídas |
| `DELETE` | `/api/notifications/{id}` | Eliminar notificación |

## Tipos de notificación

| Tipo | Descripción |
|------|-------------|
| `ORDER_CREATED` | Nueva orden creada |
| `ORDER_CONFIRMED` | Orden confirmada tras pago exitoso |
| `ORDER_CANCELLED` | Orden cancelada |
| `PAYMENT_COMPLETED` | Pago procesado con éxito |
| `PAYMENT_FAILED` | Error al procesar el pago |
| `PAYMENT_REFUNDED` | Pago reembolsado |

## Ejemplo de uso

**Crear notificación:**
```bash
curl -X POST http://localhost:8089/api/notifications \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "type": "PAYMENT_COMPLETED",
    "message": "Tu pago de $1499.99 fue procesado exitosamente para la orden #3"
  }'
```

**Respuesta:**
```json
{
  "id": 1,
  "userId": 1,
  "type": "PAYMENT_COMPLETED",
  "message": "Tu pago de $1499.99 fue procesado exitosamente para la orden #3",
  "read": false,
  "createdAt": "2026-05-20T10:30:00"
}
```

**Ver notificaciones no leídas:**
```bash
curl http://localhost:8089/api/notifications/user/1/unread
```

**Contar no leídas:**
```bash
curl http://localhost:8089/api/notifications/user/1/unread/count
# → {"unread": 3}
```

**Marcar como leída:**
```bash
curl -X PATCH http://localhost:8089/api/notifications/1/read
```

**Marcar todas como leídas:**
```bash
curl -X PATCH http://localhost:8089/api/notifications/user/1/read-all
```

## Modelo de datos

```sql
CREATE TABLE notifications (
    id         BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id    BIGINT       NOT NULL,
    type       VARCHAR(50)  NOT NULL,
    message    VARCHAR(500) NOT NULL,
    is_read    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);
```

## Dependencias externas

Ninguna. Las notificaciones son creadas por otros microservicios llamando al endpoint `POST /api/notifications`.

## Configuración (variables de entorno Docker)

| Variable | Descripción |
|----------|-------------|
| `SPRING_DATASOURCE_URL` | URL de conexión a PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | Usuario de la base de datos |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de la base de datos |

## Tecnologías

- Java 25 · Spring Boot 4.0.6
- Spring Data JPA · Hibernate 7
- Flyway (migraciones)
- PostgreSQL 16
- Lombok · Bean Validation
