# NovoBanco — Account Management Microservice

Microservicio bancario para gestión de cuentas y transacciones,
desarrollado como parte de la prueba técnica de NovoBanco.

---

## Tabla de contenidos

- [Stack tecnológico](#stack-tecnológico)
- [Arquitectura](#arquitectura)
- [Base de datos](#base-de-datos)
- [Escenarios de negocio](#escenarios-de-negocio)
- [Idempotencia](#idempotencia)
- [Ejecución con Docker](#ejecución-con-docker)
- [Pipeline CI/CD](#pipeline-cicd)
- [Historial de ramas](#historial-de-ramas)
- [ADR — Decisiones de arquitectura](#adr--decisiones-de-arquitectura)
- [Supuestos asumidos](#supuestos-asumidos)

---

## Stack tecnológico

| Componente    | Tecnología                      |
|---------------|---------------------------------|
| Framework     | Spring Boot                     |
| Base de datos | SQL Server 2022                 |
| ORM           | Spring Data JPA + Hibernate     |
| Contenedores  | Docker + Docker Compose         |
| Tests         | JUnit + Mockito                 |
| Build         | Maven                           |

---

## Arquitectura

Arquitectura por capas con separación estricta de responsabilidades.
Cada capa solo conoce a la inmediatamente inferior.

```
controller/   ← Entrada HTTP, validación de request
service/      ← Lógica de negocio, reglas bancarias, @Transactional
repository/   ← Acceso a datos, queries JPA
model/        ← Entidades JPA
dto/          ← Contratos de entrada y salida
exception/    ← Excepciones de dominio + GlobalExceptionHandler
enums/        ← AccountStatus, AccountType, TransactionType
```

---

## Base de datos

**Motor:** SQL Server 2022

**Justificación:**
- ACID completo con RCSI — reduce bloqueos sin sacrificar consistencia.
- Pessimistic Locking nativo — `SELECT ... WITH (UPDLOCK)` mapeado directamente desde JPA.
- Contexto enterprise — SLA Microsoft, herramientas DBA maduras, alineado al perfil de NovoBanco.

**CAP / PACELC:** Sistema CP. Ante partición de red rechaza la operación antes que responder con datos inconsistentes. En PACELC elige consistencia sobre latencia. Ambas propiedades son obligatorias en banca.

**Normalización:** Modelo en 3FN. No se desnormalizó porque el volumen de relaciones es bajo y la consistencia tiene mayor peso que la velocidad de lectura.

**Índices diseñados para los patrones de consulta del dominio:**

| Índice | Propósito |
|--------|-----------|
| `idx_transactions_account_date` | Historial paginado por cuenta |
| `idx_transactions_reference` | Búsqueda por referencia única / idempotencia |
| `idx_transactions_account_type_date` | Transferencias salientes por fecha |
| `idx_accounts_customer` | Cuentas por cliente |
| `idx_accounts_number` | Consulta de saldo por número de cuenta |

Esquema completo en [`schema.sql`](./schema.sql).

---

## Escenarios de negocio

**Saldo negativo — doble capa de protección:**
Validación en `TransactionService` antes del débito. Si falla, lanza `GlobalExceptionHndler `CHECK (balance >= 0)`.

**Cuenta inactiva:**
`TransactionService` verifica el estado antes de cualquier operación. Si la cuenta no está `ACTIVE` lanza `DomainException` con mensaje descriptivo → HTTP 400. El `GlobalExceptionHandler` centraliza todas las respuestas de error con estructura consistente.

**Transferencia parcial:**
Todo el débito + crédito ocurre dentro de un único `@Transactional`. Si cualquier parte falla, Spring hace rollback completo. La cuenta origen nunca queda debitada si el crédito no se concreta.

**Concurrencia:**
Se usa Pessimistic Locking (`@Lock(PESSIMISTIC_WRITE)`). El primer thread bloquea la fila hasta hacer commit. El segundo espera y lee el saldo actualizado, garantizando que dos retiros simultáneos no dejen saldo negativo.

---

## Idempotencia

El campo `reference` en `transactions` es un UUID único. Si el mismo request llega dos veces, el segundo insert viola el constraint `UNIQUE`. La aplicación capturaría las exepción`, consultaría la transacción existente por esa referencia y retornaría el resultado original sin doble débito.

---

## Ejecución con Docker

```bash
docker-compose -f docker-compose-novobanco.yaml up -d
```


**Variables de entorno:**

| Variable      | Default                                |
|---------------|----------------------------------------|
| `DB_URL`      | cadena de conexión de la base de datos |
| `DB_USER`     | `usuario de la base de datos`          |
| `DB_PASSWORD` | `contraseña de la base de datos`       |

---

## Pipeline CI/CD

GitHub Actions ejecuta en cada push y PR a `main`:

```
push / PR → build & test (mvn verify) → docker build & push (DockerHub)
```

Definido en `.github/workflows/ci-pipeline.yaml`.

---

## Historial de ramas

No se usó **GitHub Flow**. Para un microservicio de una sola persona en 48 horas, GitFlow agrega complejidad sin valor real.

```
main
 ├── feature/project-setup
 ├── feature/account-management
 ├── feature/transaction-management
 ├── feature/exception-handling
 └── feature/tests-and-docs
```

Commits con convención **Conventional Commits** (`feat`, `fix`, `test`, `docs`, `chore`).

---

## ADR — Decisiones de arquitectura

### ADR-001: Capas vs Hexagonal

**Contexto:** Un adaptador de entrada (REST) y uno de salida (SQL Server). Entrega en 48 horas.
**Decisión:** Arquitectura por capas.
**Razón:** Hexagonal agrega valor con múltiples adaptadores intercambiables. Aplicarlo aquí sería over-engineering. Capas es el estándar de facto en Spring Boot, reduce la curva de aprendizaje y permite entregar todo el scope en el tiempo disponible.
**Consecuencia:** Si el servicio incorporara múltiples integraciones externas, se justificaría migrar. La lógica de negocio ya vive solo en `service/`, lo que facilita esa evolución.


### ADR-003: Pessimistic vs Optimistic Locking

**Contexto:** Dos retiros simultáneos sobre la misma cuenta no pueden dejar saldo negativo.
**Opciones:** Optimistic (`@Version`) — sin bloqueo, retry en cliente. Pessimistic (`PESSIMISTIC_WRITE`) — bloquea la fila hasta commit.
**Decisión:** Pessimistic Locking.
**Razón:** En banca el costo de una inconsistencia supera el costo del bloqueo. El segundo thread espera y obtiene una respuesta determinista. Para el volumen de este servicio el overhead es aceptable.
**Consecuencia:** Transacciones atóimicas sin errores de inconsistencias.

---

## Supuestos asumidos
- **Endpoint cliente.** Se creo un endpoint para crear cliente pese que no estaba definido en el alcance para poder realizar pruebas y ejecutar de manera correcta el flujo del api.