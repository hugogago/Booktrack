# 📚 BookTrack

Aplicación web que combina un backend Spring Boot con un frontend Vue 3, usando MySQL para persistencia de datos.

---

## 🚀 Qué hace este proyecto

- Registro e inicio de sesión con JWT
- Biblioteca personal por usuario con CRUD de libros
- Dashboard con métricas de lectura mensual y anual
- Estadísticas por géneros y tiempos de lectura
- Catálogo compartido de libros que todos los usuarios pueden consultar

---

## 🧱 Tecnologías principales

| Tecnología | Versión | Uso |
|-----------|--------|-----|
| Java | 17 | Backend |
| Spring Boot | 3.4.0 | API REST |
| Spring Security | 6.x | Seguridad |
| JWT | 0.12.6 | Autenticación stateless |
| MySQL | 8.0 | Persistencia |
| Vue 3 | 3.x | Frontend |
| Vite | 5.x | Bundling |
| Docker Compose | — | Orquestación |

---

## 📁 Estructura del proyecto

```
Booktrack/                                 # raíz del repositorio
├── docker-compose.yml                     # orquesta db, backend y frontend
└── Booktrack-main/                        # carpeta del proyecto principal
    ├── README.md                         # este documento
    ├── booktrack/                         # backend Spring Boot
    │   ├── dockerfile                     # Dockerfile backend
    │   ├── pom.xml                        # dependencias backend
    │   ├── maven/                         # Maven local opcional
    │   └── src/
    │       └── main/
    │           ├── java/es/colegiocalasanz/booktrack/
    │           └── resources/application.properties
    └── frontend/                          # frontend Vue 3
        ├── Dockerfile                     # Dockerfile frontend
        ├── package.json
        ├── package-lock.json
        └── src/
            ├── App.vue
            └── api.js
```

---

## ▶️ Arrancar con Docker Compose (recomendado)

Desde la carpeta que contiene este README (`Booktrack-main`), sube una carpeta para llegar al `docker-compose.yml`:

```bash
cd ..
docker compose up --build -d
```

Luego accede a:

- Frontend: http://localhost:3000
- Backend: http://localhost:8081

Para detener:

```bash
docker compose down
```

---

## ▶️ Ejecutar local sin Docker

### Backend

```
cd booktrack
./maven/apache-maven-3.9.6/bin/mvn spring-boot:run
```

Si tienes Maven instalado globalmente, también vale:

```
cd booktrack
mvn spring-boot:run
```

### Frontend

```
cd frontend
npm install
npm run dev
```

### Base de datos

Necesitas una instancia MySQL accesible en `localhost:3306` y la base de datos `booktrack`.
La configuración de conexión está en `Booktrack-main/booktrack/src/main/resources/application.properties`.

---

## 📌 Comportamiento actual

- El catálogo usa la tabla `books` y es igual para todos los usuarios.
- La biblioteca personal se filtra por `ownerUsername`.
- No hay tests en el repositorio.
- Se han borrado los artefactos generados (`target`, `frontend/dist`, `frontend/node_modules`) y archivos de editor innecesarios (`.classpath`, `.project`, `.settings`).

---

## 🗄️ Consultas útiles

Ver libros:

```bash
docker compose exec -T db mysql -uroot -proot -D booktrack -e "SELECT id, owner_username, title, author, genre, publisher, total_pages, status FROM books;"
```

Ver usuarios:

```bash
docker compose exec -T db mysql -uroot -proot -D booktrack -e "SELECT id, username, email, annual_goal FROM users;"
```

##  BBDD

-- ============================================================
-- BookTrack - Esquema de base de datos MySQL 8.0
-- Basado en el diseño del anteproyecto (ER + diagrama relacional)
-- ============================================================

CREATE DATABASE IF NOT EXISTS booktrack
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE booktrack;

-- ------------------------------------------------------------
-- Tabla: usuario
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuario (
    id_usuario      INT             NOT NULL AUTO_INCREMENT,
    nombre          VARCHAR(100)    NOT NULL,
    email           VARCHAR(150)    NOT NULL,
    password        VARCHAR(255)    NOT NULL,
    fecha_registro  DATE            NOT NULL DEFAULT (CURRENT_DATE),
    objetivo_anual  INT             NOT NULL DEFAULT 12,
    rol             ENUM('ROLE_USER','ROLE_ADMIN') NOT NULL DEFAULT 'ROLE_USER',
    PRIMARY KEY (id_usuario),
    CONSTRAINT uq_usuario_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- Tabla: libro  (catálogo global)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS libro (
    id_libro    INT             NOT NULL AUTO_INCREMENT,
    titulo      VARCHAR(200)    NOT NULL,
    autor       VARCHAR(150)    NOT NULL,
    genero      VARCHAR(100)    NULL,
    editorial   VARCHAR(100)    NULL,
    num_paginas INT             NOT NULL,
    PRIMARY KEY (id_libro)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- Tabla: biblioteca  (relación usuario ↔ libro + estado lectura)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS biblioteca (
    id_biblioteca   INT             NOT NULL AUTO_INCREMENT,
    id_usuario      INT             NOT NULL,
    id_libro        INT             NOT NULL,
    estado          ENUM('PENDIENTE','EN_CURSO','LEIDO') NOT NULL DEFAULT 'PENDIENTE',
    fecha_inicio    DATE            NULL,
    fecha_fin       DATE            NULL,
    nota            INT             NULL,
    comentarios     TEXT            NULL,
    PRIMARY KEY (id_biblioteca),
    CONSTRAINT uq_biblioteca_usuario_libro UNIQUE (id_usuario, id_libro),
    CONSTRAINT chk_biblioteca_nota CHECK (nota IS NULL OR nota BETWEEN 1 AND 10),
    CONSTRAINT fk_biblioteca_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_biblioteca_libro
        FOREIGN KEY (id_libro) REFERENCES libro (id_libro)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- Tabla: accion_administradores  (auditoría de acciones admin)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS accion_administradores (
    id_accion          INT             NOT NULL AUTO_INCREMENT,
    id_admin           INT             NOT NULL,
    accion             VARCHAR(100)    NOT NULL,
    entidad_afectada   VARCHAR(50)     NULL,
    id_entidad         INT             NULL,
    fecha_accion       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_accion),
    CONSTRAINT fk_accion_admin
        FOREIGN KEY (id_admin) REFERENCES usuario (id_usuario)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- Índices de consulta frecuente
-- ------------------------------------------------------------
CREATE INDEX idx_biblioteca_usuario    ON biblioteca (id_usuario);
CREATE INDEX idx_biblioteca_estado     ON biblioteca (estado);
CREATE INDEX idx_biblioteca_fecha_fin  ON biblioteca (fecha_fin);
CREATE INDEX idx_libro_autor           ON libro (autor);
CREATE INDEX idx_libro_genero          ON libro (genero);
