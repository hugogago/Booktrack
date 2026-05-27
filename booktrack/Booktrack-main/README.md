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

