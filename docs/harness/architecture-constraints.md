# Architecture Constraints

This project follows DDD hexagonal architecture. The repository must make the boundaries visible and mechanically checkable so future sessions do not drift.

## Module Roles

| Module | Responsibility | May Depend On |
|---|---|---|
| `digital-employee-types` | Shared enums, exceptions, primitives | External libraries only |
| `digital-employee-api` | API contracts, request/response DTOs | `types` |
| `digital-employee-domain` | Business rules, entities, aggregates, value objects, domain services, outbound interfaces | `types` |
| `digital-employee-case` | Use-case orchestration across domains | `domain` |
| `digital-employee-trigger` | HTTP controllers, jobs, listeners; route and validate only | `api`, `case`, `domain`, `types` |
| `digital-employee-infrastructure` | Technical implementations of domain ports/repositories | `domain` |
| `digital-employee-app` | Spring Boot application assembly and runtime config | `trigger`, `infrastructure` |

## Domain Rules

- Domain code must not import Infrastructure classes.
- Domain code must not import MyBatis, JDBC, Redis, HTTP clients, or Spring web/runtime APIs.
- Business decisions belong in Domain services, entities, aggregates, value objects, filters, or strategies.
- If a domain needs data, define an interface in `domain/{context}/adapter/repository`.
- If a domain needs an external service, define an interface in `domain/{context}/adapter/port`.

## Infrastructure Rules

Allowed top-level packages under `infrastructure`:

- `adapter`
- `dao`
- `redis`
- `gateway`
- `config`

Required placement:

- Repository implementations: `infrastructure/adapter/repository`
- Port implementations: `infrastructure/adapter/port`
- DAO interfaces: `infrastructure/dao`
- PO objects: `infrastructure/dao/po`
- Redis wrappers: `infrastructure/redis`
- HTTP/RPC clients: `infrastructure/gateway`
- Remote DTOs: `infrastructure/gateway/dto`

Forbidden packages:

- `persistent`
- `persistent/repository`
- `persistent/dao`
- `persistent/po`
- `scenario`

## Infrastructure Internal Call Rules

- `adapter/port` may call `gateway`.
- `adapter/port` must not call `dao` or `redis`.
- `adapter/repository` may call `dao` and `redis`.
- `adapter/repository` must not call `gateway`.

## Trigger And Case Rules

- Trigger validates request shape, converts DTOs, calls Case or Domain, and returns responses.
- Trigger must not call DAO, Redis, Gateway, or Infrastructure implementation classes.
- Case orchestrates multiple Domain services and transactions.
- Case must not call DAO, Redis, Gateway, or Infrastructure implementation classes.

## Mechanical Check

Run:

```bash
bash scripts/check-architecture.sh
```

The error messages are written for agents: they explain the violated rule and the expected fix location.

