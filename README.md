# superlaptop-backend-challenge

REST API para administrar el inventario de laptops de una organización, registrar incidentes de hardware y generar reportes de los componentes que presentan más fallas.

El proyecto implementa una arquitectura tradicional de Spring Boot, con persistencia relacional en PostgreSQL y pruebas de integración que usan una instancia PostgreSQL real mediante Testcontainers.

## Badges

[![Build](https://img.shields.io/badge/build-Maven%20verify-brightgreen)](.github/workflows/tests.yml)
[![Coverage Status](https://coveralls.io/repos/github/Wilgab26/Challenge_SuperLaptop_Backend/badge.svg)](https://coveralls.io/github/Wilgab26/Challenge_SuperLaptop_Backend)
[![Java](https://img.shields.io/badge/Java-21-blue)](https://www.oracle.com/java/technologies/downloads/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-6DB33F)](https://spring.io/projects/spring-boot)

> Coveralls publica el porcentaje de cobertura después de que GitHub Actions ejecuta los tests y genera el reporte JaCoCo.

## Features

| Funcionalidad | Estado | Endpoint |
| --- | --- | --- |
| Create Laptop | Disponible | `POST /api/laptops` |
| Get Laptops | Disponible | `GET /api/laptops` |
| Get Laptop By Id | Disponible | `GET /api/laptops/{id}` |
| Update Laptop | Pendiente | — |
| Delete Laptop | Pendiente | — |
| Register Incidents | Disponible | `POST /api/incidents` |
| Get Incidents | Parcial: consulta por ID | `GET /api/incidents/{id}` |
| Report Most Damaged Components | Disponible | `GET /api/reports/component-failures` |
| Swagger Documentation | Disponible | `/swagger-ui.html` |

También existen endpoints para crear y consultar sedes, usuarios, componentes de hardware y aplicaciones. Estos catálogos se usan al crear laptops y registrar incidentes.

## Architecture

Arquitectura tradicional por capas, simple y orientada a responsabilidades:

- **controller**: expone los endpoints REST y convierte solicitudes/respuestas HTTP.
- **service**: contiene las reglas de negocio y coordina operaciones transaccionales.
- **repository**: acceso a datos con Spring Data JPA.
- **entity**: entidades y relaciones persistidas en PostgreSQL.
- **dto**: modelos de entrada y salida de la API.
- **exception**: errores de dominio y respuestas de error centralizadas.

No se añaden capas de Clean Architecture ni arquitectura hexagonal.

```text
src
├── main
│   ├── java/com/company/slaptop
│   │   ├── controller
│   │   ├── service
│   │   ├── repository
│   │   ├── dto
│   │   ├── entity
│   │   └── exception
│   └── resources
│       └── application.properties
└── test
    ├── java/com/company/slaptop
    │   ├── BaseIntegrationTest.java
    │   └── SlaptopApplicationTests.java
    └── resources
        └── application-test.yml
```

`BaseIntegrationTest` comparte la configuración de Spring, `MockMvc`, `ObjectMapper`, el PostgreSQL de Testcontainers y la limpieza de datos entre pruebas.

## Tech Stack

- Java 21
- Spring Boot 3.4.5
- Maven
- Spring Web, Spring Data JPA y Jakarta Bean Validation
- PostgreSQL 16
- Lombok
- springdoc-openapi / Swagger UI
- JUnit 5, Mockito y Spring MockMvc
- Testcontainers para PostgreSQL
- Docker y Docker Compose

## Domain Model

- **Laptop** pertenece a una sede y puede tener un usuario asignado.
- **Laptop** puede relacionarse con varios componentes y aplicaciones instaladas.
- **Incident** pertenece a una laptop y registra fecha y descripción.
- **DamagedComponent** vincula un incidente con cada componente de hardware dañado.
- El reporte cuenta los registros de componentes dañados y los ordena de mayor a menor cantidad de fallas.

## Getting Started

### Prerequisites

- JDK 21
- Docker Desktop con Docker Engine iniciado
- Maven Wrapper incluido en el repositorio

### Start PostgreSQL locally

Desde la raíz del proyecto:

```powershell
docker compose up -d
```

El servicio publica PostgreSQL en `localhost:5432` y crea la base de datos `slaptop`. Para detener el servicio sin borrar los datos persistidos:

```powershell
docker compose down
```

Los datos se conservan en el volumen `slaptop-postgres-data`. No uses `docker compose down -v` salvo que quieras borrar ese volumen y su contenido.

### Run the API

```powershell
.\mvnw.cmd spring-boot:run
```

La API queda disponible en `http://localhost:8080`. La configuración local predeterminada se puede sobrescribir con estas variables:

| Variable | Valor por defecto |
| --- | --- |
| `DB_URL` | `jdbc:postgresql://localhost:5432/slaptop` |
| `DB_USERNAME` | `postgres` |
| `DB_PASSWORD` | `postgres` |

Las credenciales de Docker Compose son solo para desarrollo local; configúralas de forma segura fuera de ese entorno. Hibernate usa `ddl-auto=update` para facilitar la ejecución local; para producción se recomienda administrar el esquema con migraciones.

## API

### Catalogs

| Método | Endpoint | Descripción |
| --- | --- | --- |
| `POST` | `/api/sedes` | Crear sede |
| `GET` | `/api/sedes` | Listar sedes |
| `GET` | `/api/sedes/{id}` | Consultar sede |
| `POST` | `/api/usuarios` | Crear usuario |
| `GET` | `/api/usuarios` | Listar usuarios |
| `GET` | `/api/usuarios/{id}` | Consultar usuario |
| `POST` | `/api/componentes` | Crear componente de hardware |
| `GET` | `/api/componentes` | Listar componentes |
| `GET` | `/api/componentes/{id}` | Consultar componente |
| `POST` | `/api/aplicaciones` | Crear aplicación |
| `GET` | `/api/aplicaciones` | Listar aplicaciones |
| `GET` | `/api/aplicaciones/{id}` | Consultar aplicación |

### Laptops, incidents and reports

| Método | Endpoint | Descripción |
| --- | --- | --- |
| `POST` | `/api/laptops` | Crear laptop |
| `GET` | `/api/laptops` | Listar laptops |
| `GET` | `/api/laptops/{id}` | Consultar laptop |
| `POST` | `/api/incidents` | Registrar incidente y componentes dañados |
| `GET` | `/api/incidents/{id}` | Consultar incidente |
| `GET` | `/api/reports/component-failures` | Reporte de fallas por componente |

Swagger UI: `http://localhost:8080/swagger-ui.html`  
OpenAPI JSON: `http://localhost:8080/v3/api-docs`

### Create a laptop

Los IDs enviados deben corresponder a catálogos existentes. Crea primero la sede, el usuario, los componentes y las aplicaciones que necesites, o consulta sus IDs con los endpoints `GET`.

```json
{
  "ip": "192.168.1.20",
  "nombre": "SL-001",
  "marca": "Lenovo",
  "modelo": "ThinkPad T14",
  "estado": "ACTIVA",
  "sedeId": 1,
  "usuarioAsignadoId": 1,
  "componenteIds": [1, 2],
  "aplicacionIds": [1]
}
```

`sedeId` es obligatorio. `usuarioAsignadoId`, `componenteIds` y `aplicacionIds` son opcionales. Los estados permitidos son `ACTIVA`, `INACTIVA`, `EN_REPARACION` y `BAJA`.

### Register an incident

```json
{
  "laptopId": 1,
  "descripcion": "El disco presenta errores de lectura",
  "componenteDanadoIds": [1]
}
```

`fecha` es opcional; si no se envía, se asigna la fecha y hora actuales.

### Error responses

Los errores de validación, conflictos de integridad y recursos inexistentes se devuelven como JSON con estado HTTP apropiado (`400`, `404` o `409`), mensaje y detalles de validación cuando corresponda.

## Testing

La suite de integración ejecuta la aplicación Spring y `MockMvc` contra un contenedor PostgreSQL 16; no usa H2 ni requiere que la base local `slaptop` esté disponible. Docker debe estar activo antes de ejecutar estos tests.

En Windows:

```powershell
.\mvnw.cmd clean verify
```

En Linux/macOS:

```bash
./mvnw clean verify
```

En IntelliJ, ejecuta `SlaptopApplicationTests`; no ejecutes `BaseIntegrationTest` directamente, porque es la clase base compartida.

Los casos actuales cubren el flujo de creación y consulta de laptop, registro de incidente y reporte, catálogo, validación, referencias inexistentes y conflictos por correo e IP duplicados. GitHub Actions ejecuta `clean verify` en cada push y pull request.

JaCoCo genera el reporte XML en `target/site/jacoco/jacoco.xml` durante `verify`. GitHub Actions lo envía a Coveralls al completar correctamente la suite. Para que el badge muestre datos, habilita el repositorio `Wilgab26/Challenge_SuperLaptop_Backend` en Coveralls. La dependencia de Mockito está disponible en `spring-boot-starter-test`; todavía no hay pruebas unitarias de servicios con mocks.

## Project Status

La API cubre el alta y consulta de laptops, catálogos, incidentes y reporte de fallas. La actualización/eliminación de laptops y el listado completo de incidentes no están implementados actualmente. Se mantiene el alcance simple del reto y se documentan estas limitaciones explícitamente.
